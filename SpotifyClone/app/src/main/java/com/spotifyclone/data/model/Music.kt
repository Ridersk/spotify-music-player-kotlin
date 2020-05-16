package com.spotifyclone.data.model

import android.content.ContentUris

class Music {
    var name: String = "Unknown Title"
    var artist: String = "Unknown Artist"
    var album: String = "Unknown Album"
    var contentUriId: Long = -1L

    constructor(title: String? = "", artist: String? = "", album: String? = "", contentUriId: Long? = -1L) {

        if (title != null && title.isNotEmpty()) {
            this.name = title
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
    }
}
