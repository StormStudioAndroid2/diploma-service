package com.example.demo.storage

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("storage")
class StorageProperties {

    /**
     * Folder location for storing files
     */
    var location = "/Users/sergeykudinov/IdeaProjects/diploma_project/upload-dir"

}
