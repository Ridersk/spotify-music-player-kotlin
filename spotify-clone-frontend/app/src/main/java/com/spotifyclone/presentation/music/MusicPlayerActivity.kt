package com.spotifyclone.presentation.music

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import androidx.viewpager2.widget.ViewPager2
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.tools.musicplayer.MusicObserver
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import com.spotifyclone.presentation.musicqueue.MusicQueueActivity
import kotlinx.android.synthetic.main.activity_music_player.*
import androidx.palette.graphics.Palette
import android.graphics.Bitmap
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.spotifyclone.tools.filemanager.MusicFileManagerApp
import com.spotifyclone.tools.utils.ColorUtils


class MusicPlayerActivity : BaseActivity(), MusicObserver {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(this@MusicPlayerActivity)
    private lateinit var itemAlbumArtAdapter: ItemAlbumArtAdapter
    private val callbackViewPagerAlbumArt = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            val currentPosition = itemAlbumArtAdapter.currentPosition
            if (position != currentPosition) {
                if (position > currentPosition) {
                    playlistMusicPlayer.nextMusic()
                } else playlistMusicPlayer.previousMusic()
                super.onPageSelected(position)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.activity_music_player)
        super.setTransitions(Slide(Gravity.BOTTOM), Slide(Gravity.BOTTOM))

        setupToolbar(
            ToolbarParameters(
                title = intent.getStringExtra(EXTRA_PLAYLIST),
                subTitle = getString(R.string.toolbar_subTitle_library),
                option1 = Pair(R.drawable.ic_arrow_down, { super.onBackPressed() }),
                option3 = Pair(R.drawable.ic_options, {})
            )
        )

        super.onCreate(savedInstanceState)
        startMusic()
        createViewPagerAlbumArt()
        playlistMusicPlayer.addMusicObserver(this)
    }

    override fun changedMusic(music: Music) {
        music.albumUriId?.let {
            this.reload(
                music.title,
                music.artist,
                it,
                intent.getStringExtra(EXTRA_PLAYLIST)
            )
        }
    }

    override fun changedMusicTimer(time: String) {
        runOnUiThread { textMusicCurrentTime.text = time }
    }

    override fun initComponents() {
        textMusicTitle.text = intent.getStringExtra(EXTRA_TITLE)
        textMusicTitle.isSelected = true
        textMusicArtist.text = intent.getStringExtra(EXTRA_ARTIST)

        textMusicTotalTime.text = playlistMusicPlayer.getTotalTime()

        progressBarMusic.setOnSeekBarChangeListener(playlistMusicPlayer.progressControl)

        buttonFavoriteMusic.setOnClickListener {
            buttonFavoriteMusic.isActivated = !buttonFavoriteMusic.isActivated
        }

        buttonPlayMusic.isActivated = playlistMusicPlayer.isPlaying
        buttonPlayMusic.setOnClickListener {
            playlistMusicPlayer.tooglePlayMusic()
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
            this.intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            this@MusicPlayerActivity.startActivity(
                intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            )
        }
    }

    override fun changedProgress(progress: Int) {
        progressBarMusic.progress = progress
    }

    override fun changedMusicState() {
        buttonPlayMusic.isActivated = playlistMusicPlayer.isPlaying
    }

    private fun createViewPagerAlbumArt() {
        val imageList: List<Long?> =
            playlistMusicPlayer.normalMusicQueueRunning.map { music -> music.albumUriId }

        itemAlbumArtAdapter = ItemAlbumArtAdapter(this, imageList, containerAlbumArt)

        containerAlbumArt.adapter = itemAlbumArtAdapter
        containerAlbumArt.registerOnPageChangeCallback(callbackViewPagerAlbumArt)

        val currentMusic = playlistMusicPlayer.positionPlaying
        containerAlbumArt.post {
            itemAlbumArtAdapter.update(currentMusic, false)
        }
    }

    override fun removeComponents() {
        playlistMusicPlayer.removeMusicObserver(this)
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
        updateAlbumArt()
        generateGradientBackground(albumUriId)
    }

    private fun generateGradientBackground(albumUriId: Long) {
        val imageBitmap: Bitmap? = MusicFileManagerApp.getAlbumArt(
            albumUriId,
            applicationContext
        )

        imageBitmap?.let {
            Palette.from(imageBitmap).generate {
                it?.let { palette ->

                    backgroundPanel.background = ColorUtils.getGradient(
                        palette.getDominantColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.lightGray
                            )
                        ),
                        ContextCompat.getColor(applicationContext, R.color.black)
                    )

                    val animation = AnimationUtils.loadAnimation(
                        this,
                        R.anim.fade_in
                    )
                    animation.duration = 1000
                    backgroundPanel.startAnimation(animation)
                }
            }
        }
    }

    private fun updateAlbumArt() {
        val currentMusic = playlistMusicPlayer.positionPlaying

        containerAlbumArt.post {
            itemAlbumArtAdapter.update(currentMusic)
        }
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
            playlist: String? = null
        ): Intent {
            return Intent(context, MusicPlayerActivity::class.java).apply {
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_ARTIST, artist)
                putExtra(EXTRA_ALBUM_URI_ID, albumUriId)
                if (playlist != null) {
                    putExtra(EXTRA_PLAYLIST, playlist)
                }
            }
        }

    }

}
