package com.example.demo.storage

open class StorageException(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause) {


}