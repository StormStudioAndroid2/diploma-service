package com.example.demo.converter

import com.example.demo.book.FictionBookImpl
import com.kursx.parser.fb2.EmptyLine
import com.kursx.parser.fb2.FictionBook
import com.kursx.parser.fb2.P
import com.kursx.parser.fb2.Section
import edu.stanford.nlp.pipeline.CoreDocument
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import org.xml.sax.SAXException
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*
import javax.xml.parsers.ParserConfigurationException


object Fb2Formatter {
    private val regex = Regex("[^A-Za-z0-9]")
    val stopWordsList = FileReader("stopwords.txt").readLines()
    const val KEY_COUNT_OF_ALL_WORDS = "-"
    private var currentCoreDocument: CoreDocument? = null
    private val pipeline: StanfordCoreNLP

    init {
        val props = Properties()
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma")
        props.setProperty("ner.applyNumericClassifiers", "false")
        props.setProperty("ner.applyNumericClassifiers", "false")
        props.setProperty("ner.useSUTime", "false")
        pipeline = StanfordCoreNLP(props)
    }

    fun getStringBufferFromBookLemmatization(bookFile: File): Map<String, Long> {
        val stringBuffer = StringBuffer()
        try {
            val fb2 = FictionBookImpl(bookFile)
            for (body in fb2.getBodyList()) {
                for (section in body.sections) {
                    getText(section, stringBuffer)
                }
            }
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: SAXException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val document: CoreDocument = pipeline.processToCoreDocument(stringBuffer.toString())

        pipeline.annotate(document)
        var countOfAllWords = 0L
        val wordMap = mutableMapOf<String, Long>()
        document.tokens().forEach {
            if (!it.lemma().contains(regex) && !stopWordsList.contains(it.lemma().lowercase(Locale.getDefault()))) {
                countOfAllWords++
                if (!it.lemma().isEmpty()) {
                    wordMap[it.lemma()] = wordMap[it.lemma()]?.inc() ?: 1
                } else {
                    wordMap[it.word()] = wordMap[it.word()]?.inc() ?: 1
                }
            }
        }
        wordMap[KEY_COUNT_OF_ALL_WORDS] = countOfAllWords
        currentCoreDocument = document
        return wordMap
    }

    fun getFormattedTextFromBook(bookFile: File): String {
        val stringBuffer = StringBuffer()
        try {
            val fb2 = FictionBookImpl(bookFile)
            for (body in fb2.getBodyList()) {
                for (section in body.sections) {
                    printSectionFormat(section, stringBuffer)
                }
            }
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: SAXException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return stringBuffer.toString()
    }

    fun getEntitiesFromDocument(): String {
        return currentCoreDocument?.let { document ->
            val stringBuffer = StringBuffer()
            document.tokens().filter { it.ner() == "PERSON" }.forEach {
                stringBuffer.append(it.word() + " ")
            }
            return stringBuffer.toString()
        } ?: throw Exception("current document is null!")
    }

    fun getTitleFromBook(bookFile: File): String {
        try {
            val fb2 = FictionBook(bookFile)
            return fb2.title
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun printSectionFormat(section: Section, stringBuffer: StringBuffer) {
        section.titles.toList().forEach { title ->
            var titleText = ""
            title.paragraphs.forEach { titleText = titleText + it.text + " " }
            val text1 = titleText.replace("\t", "")
            stringBuffer.append("<title>$text1</title>\n")
        }

        section.elements.forEach {
            when (it) {
                is P -> {
                    it.text?.let { text ->
                        val text1 = text.replace("\t", "")
                        stringBuffer.append("<p>$text1</p>\n")
                    }
                }
                is EmptyLine -> {
                    stringBuffer.append("<empty>\n")
                }
            }

        }
        for (s in section.sections) {
            printSectionFormat(s, stringBuffer)
        }
    }

    private fun printSection(section: Section, stringBuffer: StringBuffer) {
        section.titles.toList().forEach { title ->
            var titleText = ""
            title.paragraphs.forEach { titleText = titleText + it.text + " " }
            val text1 = titleText.replace("\t", "")
            stringBuffer.append("$text1 ")
        }

        section.elements.forEach {
            when (it) {
                is P -> {
                    it.text?.let { text ->
                        val text1 = text.replace("\t", "")
                        stringBuffer.append("$text1 ")
                    }
                }
                is EmptyLine -> {
                }
            }

        }
        for (s in section.sections) {
            printSection(s, stringBuffer)
        }
    }

    private fun getText(section: Section, stringBuffer: StringBuffer) {
        section.titles.toList().forEach { title ->
            var titleText = ""
            title.paragraphs.forEach { titleText = titleText + it.text + " " }
            val text1 = titleText.replace("\t", "")
            stringBuffer.append("$text1 ")
        }

        section.elements.forEach {
            when (it) {
                is P -> {
                    it.text?.let { text ->
                        val text1 = text.replace("\t", "")
                        stringBuffer.append("$text1 ")
                    }
                }
            }
        }
        for (s in section.sections) {
            printSection(s, stringBuffer)
        }
    }
}