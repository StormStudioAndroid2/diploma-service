package com.example.demo.converter

import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object FileConverter {
    @Throws(IOException::class)
    fun convert(file: MultipartFile): File? {
        val convFile = File(file.originalFilename)
        if (convFile.exists()) {
            convFile.delete()
        }
        convFile.createNewFile()
        FileOutputStream(convFile).use { os -> os.write(file.getBytes()) }
        return convFile
    }
}