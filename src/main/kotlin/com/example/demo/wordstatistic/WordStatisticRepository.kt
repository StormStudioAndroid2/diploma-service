package com.example.demo.wordstatistic

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional


@Repository
interface WordStatisticRepository : CrudRepository<WordStatistic, Long> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
        "INSERT INTO word_statistic (word, frequency) VALUES (:word1, :frequency1)\n" +
                "ON CONFLICT (word)\n" +
                "DO\n" +
                "UPDATE SET frequency =  word_statistic.frequency + EXCLUDED.frequency WHERE word_statistic.word = :word1",
        nativeQuery = true
    )
    fun addFrequencyToWord(word1: String, frequency1: Long)

    @Transactional
    @Query("SELECT word as word, ndoc as docs, nentry as frequency FROM ts_stat('SELECT book_tsvec_text FROM book') WHERE word = :word1", nativeQuery = true)
    fun getCountOfDocuments(word1: String) : WordDocumentStatistic?

    @Query("SELECT * FROM word_statistic WHERE word = :word1", nativeQuery = true)
    fun findWordStatisticByWord(word1: String): WordStatistic?

    @Query("SELECT word as word, ndoc as docs, nentry as frequency FROM ts_stat('SELECT book_tsvec_text FROM book')", nativeQuery = true)
    fun getWordDocumentStatistic() : List<WordDocumentStatistic>?
}