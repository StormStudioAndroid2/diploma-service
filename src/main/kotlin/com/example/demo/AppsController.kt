package com.example.demo

import com.example.demo.book.*
import com.example.demo.converter.Fb2Formatter
import com.example.demo.converter.FileConverter
import com.example.demo.converter.MapConverter
import com.example.demo.dictionary.DictionaryService
import com.example.demo.storage.StorageFileNotFoundException
import com.example.demo.storage.StorageService
import com.example.demo.wordstatistic.WordStatisticService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.io.IOException
import java.util.*
import java.util.logging.Logger
import java.util.stream.Collectors


@CrossOrigin(maxAge = 3600)
@RestController
class AppsController @Autowired constructor(
    private val bookService: BookService,
    private val storageService: StorageService,
    private val dictionaryService: DictionaryService,
    private val wordStatisticService: WordStatisticService
) {
    private val logger: Logger = Logger.getLogger(AppsController::class.java.getName())

    @GetMapping("/")
    @Throws(IOException::class)
    fun listUploadedFiles(): ResponseEntity<String> {
        return ResponseEntity<String>(storageService.loadAll().map { path ->
            MvcUriComponentsBuilder.fromMethodName(
                AppsController::class.java, "serveFile", path.getFileName().toString()
            ).build().toUri().toString()
        }.collect(Collectors.toList()).toString(), HttpStatus.OK)
    }

    @GetMapping("/books/{title:.+}")
    fun getBookByTitle(@PathVariable title: String): ResponseEntity<Book> {
        return bookService.getBookByTitle(title.replace("_", " "))
    }

    @PostMapping("/dictionary/load")
    fun handleJsonUpload(
        @RequestParam("file") file: MultipartFile, redirectAttributes: RedirectAttributes
    ): ResponseEntity<String> {
        var content = String(file.bytes)
        return try {
            val list = content.split(" ", "\n")
            list.forEach {
                dictionaryService.addWord(it)
            }
            ResponseEntity<String>(list.toString(), HttpStatus.OK)
        } catch (ex: Exception) {
            ResponseEntity<String>(ex.toString(), HttpStatus.BAD_REQUEST)
        }
    }

    @PostMapping("/books/load")
    fun handleFileUpload(
        @RequestParam("file") file: MultipartFile, redirectAttributes: RedirectAttributes
    ): ResponseEntity<String> {
        storageService.store(file)
        val convFile = FileConverter.convert(file)
        convFile?.let {
            val lemmatizationBookList = Fb2Formatter.getStringBufferFromBookLemmatization(convFile)
            val title = Fb2Formatter.getTitleFromBook(convFile)
            val formattedText = Fb2Formatter.getFormattedTextFromBook(convFile)
            val mentions = Fb2Formatter.getEntitiesFromDocument()
            bookService.addBookWithText(
                title,
                MapConverter.fromStringMap(lemmatizationBookList),
                lemmatizationBookList.size,
                formattedText,
                mentions
            )
            return ResponseEntity<String>("ok", HttpStatus.CREATED)
        }
        return ResponseEntity<String>(HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/books/load_text")
    fun handleFileUploadText(
        @RequestParam("file") file: MultipartFile, redirectAttributes: RedirectAttributes
    ): ResponseEntity<String> {
        storageService.store(file)
        val convFile = FileConverter.convert(file)
        convFile?.let {
            val text = Fb2Formatter.getFormattedTextFromBook(convFile)
            return ResponseEntity<String>(text, HttpStatus.OK)
        }
        return ResponseEntity<String>(HttpStatus.BAD_REQUEST)
    }

    @GetMapping("books/{title:.+}/strange_words")
    fun getAllStrangeWords(
        @PathVariable title: String
    ): ResponseEntity<String> {
        bookService.getBookByTitle(title.replace("_", " ")).body?.let {
            return ResponseEntity<String>(
                it.vectorText, HttpStatus.OK
            )

        }
        return ResponseEntity<String>(HttpStatus.NOT_FOUND)
    }

    @GetMapping("books/{title:.+}/simple_text")
    fun getSimpleText(
        @PathVariable title: String
    ): ResponseEntity<String> {
        bookService.getBookByTitle(title.replace("_", " ")).body?.let {
            return ResponseEntity<String>(
                it.text, HttpStatus.OK
            )

        }
        return ResponseEntity<String>(HttpStatus.NOT_FOUND)
    }

    @GetMapping("books/{title:.+}/important_strange_words")
    fun getImportantStrangeWords(
        @PathVariable title: String
    ): ResponseEntity<BookStrangeWordList> {
        val dictionary = dictionaryService.getWords().toSet()
        val response = this.countTfIdf(title.replace("_", " "))
        response.body?.let {
            val resultMap = it.wordMap
            val list = resultMap.toList().sortedByDescending { (_, value) -> value }.subList(0, resultMap.size / 10)
                .map { it.first }
                .filter { !dictionary.contains(it.lowercase(Locale.getDefault())) }
            return ResponseEntity<BookStrangeWordList>(
                BookStrangeWordList(
                    title, list
                ), HttpStatus.OK
            )
        }
        return ResponseEntity<BookStrangeWordList>(response.statusCode)
    }

    @GetMapping("books/{title:.+}/count_tfidf")
    fun countTfIdf(
        @PathVariable title: String
    ): ResponseEntity<BookStatistic> {
        bookService.getBookByTitle(title.replace("_", " ")).body?.let {
            val resultMap = mutableMapOf<String, Double>()
            val map = MapConverter.fromString(it.text)
            val documentCount = bookService.getBookCount().body ?: 0
            val documentStatistic = wordStatisticService.getWordDocumentStatistic().body
                ?: return ResponseEntity<BookStatistic>(HttpStatus.INTERNAL_SERVER_ERROR)
            val documentStatisticMap = mutableMapOf<String, Int>()
            documentStatistic.forEach {
                documentStatisticMap[it.getWord()] = it.getDocs()
            }
            val allWords = map[Fb2Formatter.KEY_COUNT_OF_ALL_WORDS]
                ?: return ResponseEntity<BookStatistic>(HttpStatus.INTERNAL_SERVER_ERROR)
            map.forEach { word, count ->
                if (word != Fb2Formatter.KEY_COUNT_OF_ALL_WORDS) {
                    val tf = count.toDouble() / allWords
                    val idf = Math.log(documentCount.toDouble() / (documentStatisticMap[word] ?: 1))
                    val tfIdf = tf * idf
                    resultMap[word] = tfIdf
                }
            }
            val list = resultMap.toList().sortedByDescending { (_, value) -> value }
            return ResponseEntity<BookStatistic>(
                BookStatistic(
                    title, list.toMap()
                ), HttpStatus.OK
            )
        }
        return ResponseEntity<BookStatistic>(HttpStatus.NOT_FOUND)
    }

    @GetMapping("books/list")
    fun getAllBookTitles(): ResponseEntity<String> {
        return ResponseEntity<String>(
            bookService.getBooks().map { it.title }.toString(), HttpStatus.OK
        )
    }

    @GetMapping("books/{title:.+}/format_text")
    fun getFormattedBookText(@PathVariable title: String): ResponseEntity<BookFormattedObject> {
        bookService.getFormattedBook(title).body?.let { iBookFormat ->
            return ResponseEntity<BookFormattedObject>(
                BookFormattedObject(
                    iBookFormat.getTitle(), iBookFormat.getTextformat()
                ), HttpStatus.OK
            )
        }
        return ResponseEntity<BookFormattedObject>(HttpStatus.NOT_FOUND)
    }

    @PostMapping("books/load_test")
    fun loadTest(
        @RequestParam("file") file: MultipartFile, redirectAttributes: RedirectAttributes
    ): ResponseEntity<String> {
        storageService.store(file)
        val convFile = FileConverter.convert(file)
        convFile?.let {
            val lemmatizationBookList = Fb2Formatter.getStringBufferFromBookLemmatization(convFile)
            return ResponseEntity(lemmatizationBookList.toString(), HttpStatus.OK)
        }
        return ResponseEntity<String>(HttpStatus.NOT_FOUND)
    }

    @GetMapping("books/mentions/{word:.+}")
    fun getTitleWithMentions(@PathVariable word: String): ResponseEntity<MentionListResponse> {
        return ResponseEntity<MentionListResponse>(
            MentionListResponse(bookService.getMentions(word).map { it.getTitle() }), HttpStatus.OK
        )
    }

    @GetMapping("books/{title:.+}/mentions")
    fun getMentionsFromBook(@PathVariable title: String): ResponseEntity<List<String>> {
        bookService.getBookByTitle(title.replace("_", " ")).body?.let { book ->
            val re = Regex("[^A-Za-z]")
            return ResponseEntity(
                book.mentions.split(" ").map { re.replace(it, "").lowercase() }
                    .filter { it.isNotEmpty() && !Fb2Formatter.stopWordsList.contains(it) },
                HttpStatus.OK
            )

        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(StorageFileNotFoundException::class)
    fun handleStorageFileNotFound(exc: StorageFileNotFoundException?): ResponseEntity<*>? {
        return ResponseEntity.notFound().build<Any>()
    }
}