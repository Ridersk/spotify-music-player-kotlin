package com.spotifyclone.presentation.base

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.spotifyclone.R
import com.spotifyclone.presentation.music.MusicPlayerActivity
import com.spotifyclone.tools.filemanager.MusicFileManagerApp
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.include_toolbar.view.*


abstract class BaseActivity : AppCompatActivity() {

    private var toolbarArgs: ToolbarParameters? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        initComponents()
        super.onCreate(savedInstanceState)
    }

    protected fun setupToolbar(toolbarArgs: ToolbarParameters) {
        this.toolbarArgs = toolbarArgs

        val toolbar: Toolbar = toolbarArgs.toolbar

        toolbar.let {
            with(toolbar) {
                textToolbarTitle.text = toolbarArgs.title
                textToolbarSubtitle.text = toolbarArgs.subTitle

                if (toolbarArgs.option1 != null && toolbarArgs.option1.first > 0) {
                    iconOption1.setImageDrawable(getDrawable(toolbarArgs.option1.first))
                    iconOption1.setOnClickListener{
//                        super.onBackPressed()
                        toolbarArgs.option1.second.invoke()
                    }
                }
                if (toolbarArgs.option2 != null && toolbarArgs.option2.first > 0) {
                    iconOption2.setImageDrawable(getDrawable(toolbarArgs.option2.first))
                    iconOption2.setOnClickListener{
                        toolbarArgs.option2.second.invoke()
                    }
                }
                if (toolbarArgs.option3 != null && toolbarArgs.option3.first > 0) {
                    iconOption3.setImageDrawable(getDrawable(toolbarArgs.option3.first))
                    iconOption3.setOnClickListener{
                        toolbarArgs.option3.second.invoke()
                    }
                }
            }
            setSupportActionBar(toolbarMain)
        }
    }

    protected fun insertAlbumArt(imageAlbum: ImageView, albumUriId: Long) {
        val musicThumbnail: Bitmap? = MusicFileManagerApp.getAlbumArt(
            albumUriId,
            this@BaseActivity
        )

        if (musicThumbnail != null) {
            imageAlbum.setImageBitmap(musicThumbnail)
        } else {
            imageAlbum.setImageDrawable(getDrawable(R.drawable.img_default_album_art))
        }
    }


    protected abstract fun initComponents()
}