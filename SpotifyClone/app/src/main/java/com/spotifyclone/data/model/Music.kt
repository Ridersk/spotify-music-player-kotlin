package com.spotifyclone.data.model

class Music {
    var name: String = "Unknown Title"
    var artist: String = "Unknown Artist"
    var album: String = "Unknown Album"
    var physicStoredPath: String = ""

    constructor(title: String? = "", artist: String? = "", album: String? = "", physicStoredPath: String? = "") {

        if (title != null && title.isNotEmpty()) {
            this.name = title
        }
        if (artist != null && artist.isNotEmpty()) {
            this.artist = artist
        }
        if (album != null && album.isNotEmpty()) {
            this.album = album
        }
        if (physicStoredPath != null && physicStoredPath.isNotEmpty()) {
            this.physicStoredPath = physicStoredPath
        }
    }
}
