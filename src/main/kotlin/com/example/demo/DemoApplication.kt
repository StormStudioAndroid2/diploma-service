package com.example.demo

import com.example.demo.book.BookService
import com.example.demo.dictionary.DictionaryRepository
import com.example.demo.storage.StorageProperties
import com.example.demo.storage.StorageService
import com.example.demo.wordstatistic.WordStatisticRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties::class)
class DemoApplication {

    @Bean
    fun init(
        storageService: StorageService,
        bookService: BookService,
        dictionaryRepository: DictionaryRepository,
        wordStatisticRepository: WordStatisticRepository
    ): CommandLineRunner {
        return CommandLineRunner { args: Array<String> ->
            storageService.deleteAll()
            storageService.init()
        }
    }
}

fun main(args: Array<String>) {

    runApplication<DemoApplication>(*args)
}