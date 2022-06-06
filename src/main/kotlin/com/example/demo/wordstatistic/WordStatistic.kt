package com.example.demo.wordstatistic

import javax.persistence.*

@Entity
@Table(name = "word_statistic")
class WordStatistic(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "word", nullable = false)
    var word: String,
    @Column(name = "frequency", nullable = false)
    var frequency: Long,
)