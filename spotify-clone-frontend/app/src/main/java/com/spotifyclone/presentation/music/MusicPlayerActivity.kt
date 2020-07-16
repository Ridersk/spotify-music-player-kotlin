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
import java.util.*


class MusicPlayerActivity : BaseActivity(), MusicObserver {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(this@MusicPlayerActivity)
    private lateinit var itemAlbumArtAdapter: ItemAlbumArtAdapter
    private lateinit var idCallbackStateMusic: UUID
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

    init {
        playlistMusicPlayer.addMusicObserver(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        createCallbacks()
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

    private fun createViewPagerAlbumArt() {
        val imageList: List<Long?> =
            playlistMusicPlayer.normalMusicQueueRunning.map { music -> music.albumUriId }

        itemAlbumArtAdapter = ItemAlbumArtAdapter(this, imageList, containerAlbumArt)

        containerAlbumArt.adapter = itemAlbumArtAdapter
        containerAlbumArt.registerOnPageChangeCallback(callbackViewPagerAlbumArt)

        val currentMusic = playlistMusicPlayer.positionPlaying
        containerAlbumArt.post {
            itemAlbumArtAdapter.update(currentMusic)
        }
    }

    private fun createCallbacks() {
        idCallbackStateMusic = playlistMusicPlayer.setObserverOnMusicState {
            buttonPlayMusic.isActivated = playlistMusicPlayer.isPlaying
        }
    }

    override fun removeComponents() {
        playlistMusicPlayer.removeMusicObserver(this)
        playlistMusicPlayer.removeObserverMusicTime()
        playlistMusicPlayer.removeObserverProgressBar(this)
        playlistMusicPlayer.removeObserverOnMusicState(idCallbackStateMusic)
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
