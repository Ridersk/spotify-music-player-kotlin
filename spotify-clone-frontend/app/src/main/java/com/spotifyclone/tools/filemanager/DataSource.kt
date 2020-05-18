package com.spotifyclone.tools.filemanager

import com.spotifyclone.data.model.GenericData

interface DataSource {

    // TODO

    fun writeData()
    fun readData(): GenericData
    fun listFiles()
}