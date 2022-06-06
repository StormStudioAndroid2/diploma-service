package com.example.demo.book

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class BookService @Autowired constructor(private val bookRepository: BookRepository) {

    fun getBooks(): List<Book> =
        bookRepository.findAll().toList()

    fun addBook(book: Book): ResponseEntity<Book> =
        ResponseEntity.ok(bookRepository.save(book))

    fun addBookWithText(title: String, text: String, wordCount: Int, textFormat: String, mentions: String) =
        bookRepository.insertNewTextBook(title, text, wordCount, textFormat, mentions)

    fun getBookById(bookId: Long): ResponseEntity<Book> =
        bookRepository.findById(bookId).map { book ->
            ResponseEntity.ok(book)
        }.orElse(ResponseEntity.notFound().build())

    fun getBookByTitle(bookTitle: String): ResponseEntity<Book> =
        bookRepository.getBookByTitle(bookTitle)?.let { book ->
            ResponseEntity.ok(book)
        } ?: (ResponseEntity.notFound().build())

    fun deleteTask(taskId: Long): ResponseEntity<Void> =
        bookRepository.findById(taskId).map { task ->
            bookRepository.delete(task)
            ResponseEntity<Void>(HttpStatus.ACCEPTED)
        }.orElse(ResponseEntity.notFound().build())

    fun getFormattedBook(title: String): ResponseEntity<IBookFormat> {
        bookRepository.getFormattedText(title)?.let { book ->
            return ResponseEntity.ok(book)
        }
        return ResponseEntity.notFound().build()
    }

    fun getBookCount(): ResponseEntity<Int> = ResponseEntity.ok(bookRepository.getBookCount())

    fun getMentions(word: String) : List<IBookWithMentions> {
       return bookRepository.getBooksWithMentions(word)
    }
}