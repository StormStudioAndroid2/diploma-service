package com.example.demo.book

import com.kursx.parser.fb2.Body
import com.kursx.parser.fb2.FictionBook
import java.io.File

class FictionBookImpl(file: File): FictionBook(file) {

    fun getBodyList(): List<Body> = this.bodies
}