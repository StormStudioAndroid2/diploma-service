package com.example.demo.book

import javax.persistence.Embeddable

@Embeddable
interface IBookFormat {
    fun getTitle(): String
    fun getTextformat(): String
}