package com.spotifyclone.presentation.music

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.tools.filemanager.MusicFileManagerApp
import com.spotifyclone.tools.musicplayer.MusicObserver
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import kotlinx.android.synthetic.main.activity_music_player.*
import kotlinx.android.synthetic.main.activity_music_player.view.*
import kotlinx.android.synthetic.main.include_toolbar.*
import android.graphics.Bitmap
import com.spotifyclone.components.buttons.ButtonStage


class MusicPlayerActivity : BaseActivity(), MusicObserver {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(this@MusicPlayerActivity)

    init {
        playlistMusicPlayer.addObserver(this)
    }

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

    override fun chooseMusic(music: Music) {
        reloadActivity(
            music.title,
            music.artist,
            music.albumUriId,
            "Test"
        )
    }

    override fun initComponents() {
        val layout: ViewGroup = activityMusicPlayer
        val musicTitle = layout.textMusicTitle
        val musicArtist = layout.textMusicArtist
        val imageAlbum = layout.imageAlbum
        val buttonFavorite = layout.buttonFavoriteMusic
        val buttonPlay = layout.buttonPlayMusic
        val buttonPrevious = layout.buttonPreviousMusic
        val buttonNext = layout.buttonNextMusic
        val buttonRandom = layout.buttonRandomMusic
        val buttonRepeat: ButtonStage = layout.buttonRepeat
        val buttonQueue = layout.buttonMusicQueue
        val progressBar = layout.progressBarMusic
        val timer = layout.textMusicTime

        musicTitle.text = intent.getStringExtra(EXTRA_TITLE)
        musicArtist.text = intent.getStringExtra(EXTRA_ARTIST)

        insertAlbumArt(imageAlbum)

        playlistMusicPlayer.setObserverMusicTime { time: String ->
            runOnUiThread { timer.text = time }
        }

        progressBar.setOnSeekBarChangeListener(playlistMusicPlayer.progressControl)
        playlistMusicPlayer.setObserverProgressBar { progress: Int ->
            progressBar.progress = progress
        }

        buttonFavorite.setOnClickListener {
            buttonFavorite.isActivated = !buttonFavorite.isActivated
        }

        buttonPlay.isActivated = true
        buttonPlay.setOnClickListener {
            playlistMusicPlayer.tooglePlayMusic()
            buttonPlay.isActivated = playlistMusicPlayer.isPlaying
        }

        playlistMusicPlayer.setObserverOnCompletionListener {
            buttonPlay.isActivated = false
        }

        buttonPrevious.setOnClickListener {
            playlistMusicPlayer.previousMusic()
        }

        buttonNext.setOnClickListener {
            playlistMusicPlayer.nextMusic()
        }

        buttonRandom.setStatusProvider { playlistMusicPlayer.isRandom() }
        buttonRandom.setMainButtonStatesProvider { playlistMusicPlayer.getRandomType() }
        buttonRandom.setOnClickListener {
            playlistMusicPlayer.toogleRandom()
        }

        buttonRepeat.setStatusProvider {playlistMusicPlayer.isCycle()}
        buttonRepeat.setMainButtonStatesProvider { playlistMusicPlayer.getCycleType() }
        buttonRepeat.setOnClickListener {
            playlistMusicPlayer.toogleModeCycle()
        }

        buttonQueue.setOnClickListener {
        }
    }


    private fun startMusic() {
        chooseMusic(playlistMusicPlayer.getCurrentMusic())
    }

    private fun insertAlbumArt(imageAlbum: ImageView) {
        val musicThumbnail: Bitmap? = MusicFileManagerApp.getAlbumArt(
            intent.getLongExtra(EXTRA_ALBUM_URI_ID, -1),
            this@MusicPlayerActivity
        )

        if (musicThumbnail != null) {
            imageAlbum.setImageBitmap(musicThumbnail)
        } else {
            imageAlbum.setImageDrawable(getDrawable(R.drawable.img_default_album_art))
        }
    }

    private fun reloadActivity(title: String, artist: String, albumUriId: Long, playlist: String) {
        intent?.apply {
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_ARTIST, artist)
            putExtra(EXTRA_ALBUM_URI_ID, albumUriId)
            putExtra(EXTRA_PLAYLIST, playlist)
        }

        initComponents()
    }

    companion object {
        private const val EXTRA_TITLE = "EXTRA_TITLE"
        private const val EXTRA_ARTIST = "EXTRA_ARTIST"
        private const val EXTRA_PLAYLIST = "EXTRA_PLAYLIST"
        private const val EXTRA_ALBUM_URI_ID = "EXTRA_ALBUM_URI_ID"

        fun getStartIntent(
            context: Context, title: String, artist: String,
            albumUriId: Long, playlist: String
        ): Intent {
            return Intent(context, MusicPlayerActivity::class.java).apply {
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_ARTIST, artist)
                putExtra(EXTRA_ALBUM_URI_ID, albumUriId)
                putExtra(EXTRA_PLAYLIST, playlist)
            }
        }

    }

}
