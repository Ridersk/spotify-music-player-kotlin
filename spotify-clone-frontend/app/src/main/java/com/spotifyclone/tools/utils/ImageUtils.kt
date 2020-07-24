package com.spotifyclone.tools.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.spotifyclone.R
import com.spotifyclone.tools.filemanager.MusicFileManagerApp

class ImageUtils {

    companion object {
        fun getBitmapAlbumArt(context: Context, albumUriId: Long): Bitmap {
            return MusicFileManagerApp.getAlbumArt(
                        albumUriId,
                        context
                    )
                ?: BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.img_default_album_art
                )
        }
    }
}