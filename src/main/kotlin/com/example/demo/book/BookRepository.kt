package com.example.demo.book

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional


@Repository
interface BookRepository : CrudRepository<Book, Long> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
        "INSERT INTO book (book_title, book_text, book_text_pretty, book_tsvec_text, book_word_count, book_mentions) VALUES (:title, :text, :bookFormatText, to_tsvector('simple', :text), :wordCount, to_tsvector('simple', :mentions))",
        nativeQuery = true
    )
    fun insertNewTextBook(title: String, text: String, wordCount: Int, bookFormatText: String, mentions: String)

    @Query(value = "SELECT * FROM book WHERE book_title ILIKE :title LIMIT 1", nativeQuery = true)
    fun getBookByTitle(title: String): Book?

    @Transactional
    @Query(value = "select count(*) from book", nativeQuery = true)
    fun getBookCount(): Int

    @Query(
        value = "SELECT book_title as title, book_text_pretty as textformat from book WHERE book_title ILIKE :title",
        nativeQuery = true
    )
    fun getFormattedText(title: String): IBookFormat?

    @Query(
        value = "SELECT book_title as title from book WHERE book_mentions @@ to_tsquery('simple', :mention)",
        nativeQuery = true
    )
    fun getBooksWithMentions(mention: String): List<IBookWithMentions>
}