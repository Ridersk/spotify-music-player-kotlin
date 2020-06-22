package com.spotifyclone.data.model

import java.util.*

class Music(
    title: String? = "",
    artist: String? = "",
    album: String? = "",
    contentUriId: Long? = -1L,
    albumUriId: Long? = -1L
) {
    val id: UUID
    var title: String = "Unknown Title"
    var artist: String = "Unknown Artist"
    var album: String = "Unknown Album"
    var contentUriId: Long = -1L
    var albumUriId: Long = -1L

    init {
        if (title != null && title.isNotEmpty()) {
            this.title = title
        }
        if (artist != null && artist.isNotEmpty()) {
            this.artist = artist
        }
        if (album != null && album.isNotEmpty()) {
            this.album = album
        }
        if (contentUriId != null && contentUriId != -1L) {
            this.contentUriId = contentUriId
        }
        if (albumUriId != null && albumUriId != -1L) {
            this.albumUriId = albumUriId
        }

        this.id = UUID.randomUUID()
    }
}
