package com.spotifyclone.presentation.musicqueue

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import androidx.fragment.app.Fragment
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.tools.musicplayer.MusicObserver
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import com.spotifyclone.tools.utils.ImageUtils
import com.spotifyclone.tools.utils.TextUtils
import kotlinx.android.synthetic.main.activity_music_queue.*

class MusicQueueActivity : BaseActivity(), MusicObserver {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(this@MusicQueueActivity)
    private val containerFragmentOptions = mutableListOf<Fragment>()
    private var currentPositionFragmentQueueOption = 0
    private lateinit var musicQueueView: MusicQueueView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.activity_music_queue)
        super.setTransitions(Slide(Gravity.BOTTOM), Slide(Gravity.BOTTOM))

        setupToolbar(
            ToolbarParameters(
                title = intent.getStringExtra(EXTRA_PLAYLIST_NAME),
                subTitle = getString(R.string.toolbar_subTitle_library),
                option1 = Pair(R.drawable.ic_close, { super.onBackPressed() })
            )
        )
        super.onCreate(savedInstanceState)
        musicQueueView = createRecyclerViewMusicQueue()
        musicQueueView.create()
        createOptionsBottom()
        playlistMusicPlayer.addMusicObserver(this)
    }

    override fun changedMusic(music: Music) {
        music.albumUriId?.let {
            reloadActivity(
                it,
                intent.getStringExtra(EXTRA_PLAYLIST_NAME)
            )
        }
    }

    override fun initComponents() {
        val currentMusic: Music? = playlistMusicPlayer.getCurrentMusic()
        currentMusic?.let {
            textMusicTitle.text = currentMusic.title
            textMusicLabel.text = TextUtils.getMusicLabel(currentMusic.artist, currentMusic.album)

            albumArt.setImageBitmap(
                ImageUtils.getBitmapAlbumArt(
                    applicationContext,
                    intent.getLongExtra(EXTRA_ALBUM_URI_ID, -1)
                )
            )
        }
    }

    override fun removeComponents() {
        playlistMusicPlayer.removeMusicObserver(this)
    }

    private fun createOptionsBottom() {
        val musicPlayerFragment =
            MusicPlayerQueueFragment.getInstance(this)
        val modifyQueueFragment = ModifyQueueFragment.getInstance(
            { musicQueueView.addMusics() },
            { musicQueueView.removeMusics() }
        )
        containerFragmentOptions.add(musicPlayerFragment)
        containerFragmentOptions.add(modifyQueueFragment)
        supportFragmentManager.beginTransaction()
            .add(
                R.id.containerOptionsQueue,
                containerFragmentOptions[currentPositionFragmentQueueOption]
            )
            .commit()
    }

    private fun updateMusicQueue() {
        musicQueueView.update()
    }

    private fun createRecyclerViewMusicQueue(): MusicQueueView {
        return MusicQueueView(
            this@MusicQueueActivity,
            recyclerNextFromQueue,
            intent.getStringExtra(EXTRA_PLAYLIST_NAME)
        ) {
            toogleContainerOptionsQueue()
        }
    }

    private fun reloadActivity(albumUriId: Long, playlist: String? = "") {
        intent?.apply {
            putExtra(EXTRA_PLAYLIST_NAME, playlist)
            putExtra(EXTRA_ALBUM_URI_ID, albumUriId)
        }
        initComponents()
        updateMusicQueue()
    }

    private fun toogleContainerOptionsQueue() {
        currentPositionFragmentQueueOption =
            (currentPositionFragmentQueueOption + 1) % containerFragmentOptions.size
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.containerOptionsQueue,
                containerFragmentOptions[currentPositionFragmentQueueOption]
            )
            .commit()
    }

    companion object {
        private const val EXTRA_PLAYLIST_NAME = "EXTRA_PLAYLIST_NAME"
        private const val EXTRA_ALBUM_URI_ID = "EXTRA_ALBUM_URI_ID"

        fun getStartIntent(
            context: Context,
            playlist: String?,
            albumUriId: Long
        ): Intent {
            return Intent(context, MusicQueueActivity::class.java).apply {
                putExtra(EXTRA_PLAYLIST_NAME, playlist)
                putExtra(EXTRA_ALBUM_URI_ID, albumUriId)
            }
        }
    }
}
