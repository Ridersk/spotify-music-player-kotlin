package com.spotifyclone.presentation.music

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import com.example.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import kotlinx.android.synthetic.main.activity_music_player.*
import kotlinx.android.synthetic.main.activity_music_player.view.*
import kotlinx.android.synthetic.main.include_toolbar.*

class MusicPlayerActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)

        setupToolbar(
            ToolbarParameters(
                toolbar =  toolbarMain,
                title =  intent.getStringExtra(EXTRA_PLAYLIST),
                subTitle = getString(R.string.music_toolbar_subTitle),
                option1 =  Pair(R.drawable.ic_close, {super.onBackPressed()}),
                option3 =  Pair(R.drawable.ic_options, {})
            )
        )

        val layout: ViewGroup = activityMusicPlayer

        layout.textMusicName.text = intent.getStringExtra(EXTRA_NAME)
        layout.textMusicAuthor.text = intent.getStringExtra(EXTRA_AUTHOR)
    }

    companion object {
        private const val EXTRA_NAME = "EXTRA_NAME"
        private const val EXTRA_AUTHOR = "EXTRA_AUTHOR"
        private const val EXTRA_PLAYLIST = "EXTRA_PLAYLIST"

        fun getStartIntent(context: Context, name: String, author: String, playlist: String) : Intent {
            return Intent(context, MusicPlayerActivity::class.java).apply {
                putExtra(EXTRA_NAME, name)
                putExtra(EXTRA_AUTHOR, author)
                putExtra(EXTRA_PLAYLIST,  playlist)
            }
        }


    }

}
