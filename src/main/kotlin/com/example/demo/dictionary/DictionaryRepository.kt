package com.example.demo.dictionary

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DictionaryRepository : CrudRepository<Dictionary, Long> {

    @Query(value = "SELECT * FROM dictionary WHERE word = :word", nativeQuery = true)
    fun findWordInDatabase(word: String): Dictionary?
}