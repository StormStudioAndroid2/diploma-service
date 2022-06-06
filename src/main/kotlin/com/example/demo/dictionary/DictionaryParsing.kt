package com.example.demo.dictionary

import com.google.gson.Gson
import com.google.gson.JsonArray

class DictionaryParsing {

    fun parseJSON(json: String): DictionaryModel {
        try {
            return  Gson().fromJson(json, DictionaryModel::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return DictionaryModel()
    }
}