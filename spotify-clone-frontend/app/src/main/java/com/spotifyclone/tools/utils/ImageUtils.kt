package com.spotifyclone.tools.utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.spotifyclone.R
import com.spotifyclone.tools.filemanager.MusicFileManagerApp

class ImageUtils {

    companion object {
        fun insertBitmapInView(context: Context, imageView: ImageView, albumUriId: Long) {
            val imageBitmap: Bitmap? = MusicFileManagerApp.getAlbumArt(
                albumUriId,
                context
            )

            if (imageBitmap != null) {
                imageView.setImageBitmap(imageBitmap)
            } else {
                imageView.setImageDrawable(context.getDrawable(R.drawable.img_default_album_art))
            }
        }
    }
}