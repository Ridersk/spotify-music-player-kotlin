package com.spotifyclone.presentation.music

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.tools.filemanager.MusicFileManagerApp
import com.spotifyclone.tools.musicplayer.MusicObserver
import com.spotifyclone.tools.musicplayer.PlaylistController
import com.spotifyclone.tools.musicplayer.SpotifyMediaController
import kotlinx.android.synthetic.main.activity_music_player.*
import kotlinx.android.synthetic.main.activity_music_player.view.*
import kotlinx.android.synthetic.main.include_toolbar.*
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageButton
import com.spotifyclone.components.buttons.ButtonStage
import com.spotifyclone.tools.images.ImageTreatment


class MusicPlayerActivity : BaseActivity(), MusicObserver {

    private lateinit var parentContext: Context
    private val musicPlayer = SpotifyMediaController.getInstance(this@MusicPlayerActivity)
    private val playlistController = PlaylistController.getInstance(this@MusicPlayerActivity)


    init {
        playlistController.addObserver(this)
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
        musicPlayer.prepareMusic(
            playlistController.getCurrentMusic().contentUriId
        )
        musicPlayer.playMusic()
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
        val buttonRepeat: ButtonStage = layout.layoutButtonRepeat
        val buttonQueue = layout.buttonMusicQueue
        val progressBar = layout.progressBarMusic
        val timer = layout.textMusicTime

        musicTitle.text = intent.getStringExtra(EXTRA_TITLE)
        musicArtist.text = intent.getStringExtra(EXTRA_ARTIST)

        insertAlbumArt(imageAlbum)

        progressBar.setOnSeekBarChangeListener(musicPlayer.progressControl)

        musicPlayer.setObserverMusicTime { time: String ->
            runOnUiThread { timer.text = time }
        }

        musicPlayer.setObserverProgressBar { progress: Int ->
            progressBar.progress = progress
        }

        buttonFavorite.setOnClickListener {
            buttonFavorite.isActivated = !buttonFavorite.isActivated
        }

        buttonPlay.isActivated = true
        buttonPlay.setOnClickListener {
            musicPlayer.playMusic()
            buttonPlay.isActivated = musicPlayer.isPlaying
        }

        musicPlayer.setObserverOnCompletion {
            buttonPlay.isActivated = false
        }

        buttonPrevious.setOnClickListener {
            playlistController.previousMusic()
        }

        buttonNext.setOnClickListener {
            playlistController.nextMusic()
        }

        buttonRandom.isActivated = playlistController.random
        buttonRandom.setOnClickListener {
            playlistController.toogleRandom()
            buttonRandom.isActivated = playlistController.random
        }

//        val icoButtonRepeatActivated = layout.layoutButtonRepeat.buttonActivated

//        buttonRepeat.setStatusProvider {playlistController.isCycle()}

        buttonRepeat.setOnClickListener {
            playlistController.toogleModeCycle()
//            buttonRepeat.isActivated = playlistController.isCycle()
//            icoButtonRepeatActivated.visibility = if (playlistController.isCycle()) View.VISIBLE else View.INVISIBLE
        }

        buttonQueue.setOnClickListener {
        }
    }

    private fun changeButton(button: ImageButton) {
        if (button.isActivated) {
            val repeateicon: Drawable? = getDrawable(R.drawable.ic_repeat_music)
            val circleIcon = getDrawable(R.drawable.ic_circle)

            val repeatIconScale: Drawable = ImageTreatment.resizeDrawable(applicationContext, repeateicon, 15, 15)

            button.setImageDrawable(ImageTreatment.mergeIcons(repeatIconScale, circleIcon))
        }
    }





    private fun startMusic() {
        chooseMusic(playlistController.getCurrentMusic())
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
