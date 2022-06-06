package com.example.demo.wordstatistic

import javax.persistence.Embeddable

@Embeddable
interface WordDocumentStatistic {
    fun getWord(): String
    fun getFrequency(): Int
    fun getDocs(): Int
}