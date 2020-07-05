package com.spotifyclone.presentation.music

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.tools.musicplayer.MusicObserver
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import com.spotifyclone.presentation.musicqueue.MusicQueueActivity
import com.spotifyclone.tools.utils.ImageUtils
import kotlinx.android.synthetic.main.activity_music_player.*


class MusicPlayerActivity : BaseActivity(), MusicObserver {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(this@MusicPlayerActivity)

    init {
        playlistMusicPlayer.addMusicObserver(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.activity_music_player)
        super.onCreate(savedInstanceState)

        setupToolbar(
            ToolbarParameters(
                title = intent.getStringExtra(EXTRA_PLAYLIST),
                subTitle = getString(R.string.toolbar_subTitle_library),
                option1 = Pair(R.drawable.ic_close, { super.onBackPressed() }),
                option3 = Pair(R.drawable.ic_options, {})
            )
        )

        startMusic()
    }

    override fun changedMusic(music: Music) {
        music.albumUriId?.let {
            this.reload(
                music.title,
                music.artist,
                it,
                intent.getStringExtra(EXTRA_PLAYLIST)!!
            )
        }
    }

    override fun initComponents() {
        textMusicTitle.text = intent.getStringExtra(EXTRA_TITLE)
        textMusicArtist.text = intent.getStringExtra(EXTRA_ARTIST)

        ImageUtils.insertBitmapInView(
            applicationContext,
            imageAlbum,
            intent.getLongExtra(EXTRA_ALBUM_URI_ID, -1)
        )

        textMusicTotalTime.text = playlistMusicPlayer.getTotalTime()

        playlistMusicPlayer.setObserverMusicTime { time: String ->
            runOnUiThread { textMusicCurrentTime.text = time }
        }

        progressBarMusic.setOnSeekBarChangeListener(playlistMusicPlayer.progressControl)
        playlistMusicPlayer.setObserverProgressBar(this) { progress: Int ->
            progressBarMusic.progress = progress
        }

        buttonFavoriteMusic.setOnClickListener {
            buttonFavoriteMusic.isActivated = !buttonFavoriteMusic.isActivated
        }

        buttonPlayMusic.isActivated = playlistMusicPlayer.isPlaying
        buttonPlayMusic.setOnClickListener {
            playlistMusicPlayer.tooglePlayMusic()
        }

        playlistMusicPlayer.setObserverOnMusicState(this) {
            buttonPlayMusic.isActivated = playlistMusicPlayer.isPlaying
        }

        buttonPreviousMusic.setOnClickListener {
            playlistMusicPlayer.previousMusic()
        }

        buttonNextMusic.setOnClickListener {
            playlistMusicPlayer.nextMusic()
        }

        buttonRandomMusic.setStatusProvider { playlistMusicPlayer.isRandom() }
        buttonRandomMusic.setMainButtonStatesProvider { playlistMusicPlayer.getRandomType() }
        buttonRandomMusic.setOnClickListener {
            playlistMusicPlayer.toogleRandom()
        }

        buttonRepeat.setStatusProvider { playlistMusicPlayer.isCycle() }
        buttonRepeat.setMainButtonStatesProvider { playlistMusicPlayer.getCycleType() }
        buttonRepeat.setOnClickListener {
            playlistMusicPlayer.toogleModeCycle()
        }

        buttonMusicQueue.setOnClickListener {
            val intent = MusicQueueActivity.getStartIntent(
                this@MusicPlayerActivity,
                intent.getStringExtra(EXTRA_PLAYLIST),
                intent.getLongExtra(EXTRA_ALBUM_URI_ID, -1)
            )
            this@MusicPlayerActivity.startActivity(intent)
        }
    }

    override fun removeComponents() {
        playlistMusicPlayer.removeMusicObserver(this)
        playlistMusicPlayer.removeObserverMusicTime()
        playlistMusicPlayer.removeObserverProgressBar(this)
        playlistMusicPlayer.removeObserverOnMusicState(this)
    }


    private fun startMusic() {
        val currentMusic = playlistMusicPlayer.getCurrentMusic()
        currentMusic?.let {
            changedMusic(currentMusic)
        }
    }

    private fun reload(
        title: String,
        artist: String,
        albumUriId: Long,
        playlist: String? = ""
    ) {
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

        fun getIntent(
            context: Context,
            title: String,
            artist: String,
            albumUriId: Long,
            playlist: String
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
