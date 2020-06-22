package com.spotifyclone.tools.utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.spotifyclone.R
import com.spotifyclone.tools.filemanager.MusicFileManagerApp

class ImageUtils {

    companion object {
        fun insertBitmapInView(context: Context, imageAlbum: ImageView, albumUriId: Long) {
            val musicThumbnail: Bitmap? = MusicFileManagerApp.getAlbumArt(
                albumUriId,
                context
            )

            if (musicThumbnail != null) {
                imageAlbum.setImageBitmap(musicThumbnail)
            } else {
                imageAlbum.setImageDrawable(context.getDrawable(R.drawable.img_default_album_art))
            }
        }
    }
}