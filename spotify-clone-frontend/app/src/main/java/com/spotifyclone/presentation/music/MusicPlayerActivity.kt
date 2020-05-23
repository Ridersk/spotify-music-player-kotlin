package com.spotifyclone.presentation.music

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import com.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.tools.musicplayer.SpotifyMediaPlayer
import com.spotifyclone.tools.statemanager.ComponentStateManager
import kotlinx.android.synthetic.main.activity_music_player.*
import kotlinx.android.synthetic.main.activity_music_player.view.*
import kotlinx.android.synthetic.main.include_toolbar.*



class MusicPlayerActivity : BaseActivity() {

    private val musicPlayer = SpotifyMediaPlayer.getInstance(this@MusicPlayerActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.activity_music_player)
        super.onCreate(savedInstanceState)

        setupToolbar(
            ToolbarParameters(
                toolbar = toolbarMain,
                title = intent.getStringExtra(EXTRA_PLAYLIST),
                subTitle = getString(R.string.music_toolbar_subTitle),
                option1 = Pair(R.drawable.ic_close, { super.onBackPressed() }),
                option3 = Pair(R.drawable.ic_options, {})
            )
        )

        startMusic()
    }

    override fun initComponents() {
        val layout: ViewGroup = activityMusicPlayer
        val buttonFavorite = layout.buttonFavoriteMusic
        val buttonPlay = layout.buttonPlayMusic
        val buttonPrevious = layout.buttonPreviousMusic
        val buttonNext = layout.buttonNextMusic
        val buttonRandom = layout.buttonRandomMusic
        val buttonRepeat = layout.buttonRepeatMusic
        val buttonQueue = layout.buttonMusicQueue
        val progressBar = layout.progressBarMusic
        val timer = layout.textMusicTime

        layout.textMusicName.text = intent.getStringExtra(EXTRA_NAME)
        layout.textMusicArtist.text = intent.getStringExtra(EXTRA_AUTHOR)


        progressBar.setOnSeekBarChangeListener(musicPlayer.progressControl)

        musicPlayer.setObserverMusicTime { time: String ->
            runOnUiThread { timer.text = time }
        }

        musicPlayer.setObserverProgressBar { progress: Int ->
            progressBar.progress = progress
        }

        buttonFavorite.setOnClickListener{
        }

        val buttonPlayState = ComponentStateManager(
            this@MusicPlayerActivity,
            buttonPlay,
            listOf(R.drawable.ic_pause_music, R.drawable.ic_play_music)
        ) {
            musicPlayer.playMusic()
        }

        musicPlayer.setObserverOnCompletion {
            buttonPlayState.toggleOption()
        }

        buttonPrevious.setOnClickListener{
        }

        buttonNext.setOnClickListener{
        }

        buttonRandom.setOnClickListener{
        }

        buttonRepeat.setOnClickListener{
        }

        buttonQueue.setOnClickListener{
        }
    }

    private fun startMusic() {
        musicPlayer.prepareMusic(intent.getLongExtra(CONTENT_URI_ID, -1L))
        musicPlayer.playMusic()

    }

    companion object {
        private const val EXTRA_NAME = "EXTRA_NAME"
        private const val EXTRA_AUTHOR = "EXTRA_AUTHOR"
        private const val EXTRA_PLAYLIST = "EXTRA_PLAYLIST"
        private const val CONTENT_URI_ID = "CONTENT_URI_ID"

        fun getStartIntent(
            context: Context, name: String, author: String, contentUriId: Long, playlist: String
        ): Intent {

            return Intent(context, MusicPlayerActivity::class.java).apply {
                putExtra(EXTRA_NAME, name)
                putExtra(EXTRA_AUTHOR, author)
                putExtra(EXTRA_PLAYLIST, playlist)
                putExtra(CONTENT_URI_ID, contentUriId)
            }
        }


    }

}
