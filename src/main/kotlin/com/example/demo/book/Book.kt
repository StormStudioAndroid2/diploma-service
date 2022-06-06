package com.example.demo.book

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.*


@Entity
@Table(name = "book")
class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "book_title", nullable = false)
    var title: String,
    @Column(name = "book_text", nullable = false, columnDefinition = "text")
    var text: String,
    @Column(name = "book_tsvec_text", nullable = false, columnDefinition = "tsvector")
    var vectorText: String = "",
    @Column(name = "book_text_pretty", nullable = false, columnDefinition = "text")
    var formatText: String = "",
    @Column(name = "book_word_count", nullable = false)
    var wordCount: Int,
    @Column(name = "book_mentions", nullable = false,  columnDefinition = "tsvector")
    var mentions: String = ""
)
