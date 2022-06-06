package com.example.demo.wordstatistic

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class WordStatisticService @Autowired constructor(private val wordStatisticRepository: WordStatisticRepository) {

    fun getWordStatistic(): List<WordStatistic> =
        wordStatisticRepository.findAll().toList()

    fun updateWordFrequency(word: String, frequency: Long) =
        wordStatisticRepository.addFrequencyToWord(word, frequency)

    fun getWordStatisticId(wordStatisticId: Long): ResponseEntity<WordStatistic> =
        wordStatisticRepository.findById(wordStatisticId).map { book ->
            ResponseEntity.ok(book)
        }.orElse(ResponseEntity.notFound().build())

    fun getWordStatisticByWord(word: String): ResponseEntity<WordStatistic> =
        wordStatisticRepository.findWordStatisticByWord(word)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()

    fun delete(wordId: Long): ResponseEntity<Void> =
        wordStatisticRepository.findById(wordId).map { task ->
            wordStatisticRepository.delete(task)
            ResponseEntity<Void>(HttpStatus.ACCEPTED)
        }.orElse(ResponseEntity.notFound().build())

    fun getCountOfDocuments(word: String): ResponseEntity<Int> =
        wordStatisticRepository.getCountOfDocuments(word)?.let {
            ResponseEntity.ok(it.getDocs())
        } ?: ResponseEntity.notFound().build()

    fun getWordDocumentStatistic(): ResponseEntity<List<WordDocumentStatistic>> =
        wordStatisticRepository.getWordDocumentStatistic()?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
}