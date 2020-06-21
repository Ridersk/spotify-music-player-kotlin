package com.spotifyclone.tools.utils

class TextUtils {
    companion object {
        fun getMusicLabel(artist: String? = "", album: String? = "") =
            "$artist${showDiv(artist, album)}$album"
        private fun showDiv(author: String? = "", album: String? = "") =
            if (author.isNullOrBlank() || album.isNullOrBlank()) "" else " • "

    }
}