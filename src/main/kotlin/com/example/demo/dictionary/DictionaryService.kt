package com.example.demo.dictionary

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class DictionaryService @Autowired constructor(private val dictionaryRepository: DictionaryRepository) {

    fun getWords(): List<String> =
        dictionaryRepository.findAll().map { it.word }

    fun addWord(word: String): ResponseEntity<Dictionary> =
        ResponseEntity.ok(dictionaryRepository.save(Dictionary(null, word)))

    fun getWordById(wordId: Long): ResponseEntity<Dictionary> =
        dictionaryRepository.findById(wordId).map { book ->
            ResponseEntity.ok(book)
        }.orElse(ResponseEntity.notFound().build())

    fun findWordInDatabase(word: String): Boolean {
        dictionaryRepository.findWordInDatabase(word)?.let {
            return true
        }
        return false
    }


    fun deleteAll(): ResponseEntity<Void> {
        return try {
            dictionaryRepository.deleteAll()
            ResponseEntity(HttpStatus.OK)
        } catch (ex: Exception) {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }
}
