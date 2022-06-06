package com.example.demo.dictionary

import javax.persistence.*

@Entity
@Table(name = "dictionary")
class Dictionary (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "word", nullable = false)
    var word: String
)