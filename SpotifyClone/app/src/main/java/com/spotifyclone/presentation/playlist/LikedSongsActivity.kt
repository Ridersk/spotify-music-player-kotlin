package com.spotifyclone.presentation.playlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_liked_songs.*

class LikedSongsActivity : BaseActivity(), PlaylistInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liked_songs)

        textTitle.text = getString(R.string.liked_text_title)
        buttonRandomPlay.text = getString(R.string.liked_button_random_play)
        textDownloadedSongs.text = getString(R.string.liked_text_downloaded_songs)
    }

    companion object {
        private const val EXTRA_TITLE = "EXTRA_TITLE"

        fun getStartIntent(context: Context, title: String) : Intent {
            return Intent(context, LikedSongsActivity::class.java).apply {
                putExtra(EXTRA_TITLE, title)
            }
        }

    }
}
