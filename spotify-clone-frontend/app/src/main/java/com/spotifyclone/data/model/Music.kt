package com.spotifyclone.data.model

import com.google.gson.Gson
import java.util.*

class Music(
    title: String? = "",
    artist: String? = "",
    album: String? = "",
    val contentUriId: Long? = null,
    val albumUriId: Long? = null
) {
    val id: UUID = UUID.randomUUID()
    val title: String = if (title != null && title.isNotEmpty()) title else "Unknown Title"
    val artist: String = if (artist != null && artist.isNotEmpty()) artist else "Unknown Artist"
    val album: String = if (album != null && album.isNotEmpty()) album else "Unknown Album"

    fun deepCopy(): Music {
        val json = Gson().toJson(this)
        return Gson().fromJson(json, Music::class.java)
    }
}
