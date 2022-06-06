package com.example.demo.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object MapConverter {
    @JvmStatic
    fun fromString(value: String): Map<String, Long> {
        val mapType = object : TypeToken<Map<String, Long>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @JvmStatic
    fun fromStringMap(map: Map<String, Long>): String {
        val gson = Gson()
        return gson.toJson(map)
    }
}