package com.spotifyclone.presentation.musicqueue

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import com.spotifyclone.tools.utils.TextUtils
import kotlinx.android.synthetic.main.activity_music_queue.*
import kotlinx.android.synthetic.main.activity_music_queue.view.*
import kotlinx.android.synthetic.main.include_toolbar.*

class MusicQueueActivity : BaseActivity() {
    private val playlistController = PlaylistMusicPlayer.getInstance(this@MusicQueueActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_music_queue)
        super.onCreate(savedInstanceState)


        setupToolbar(
            ToolbarParameters(
                toolbar = toolbarMain,
                title = intent.getStringExtra(EXTRA_PLAYLIST_NAME),
                subTitle = getString(R.string.toolbar_subTitle_library),
                option1 = Pair(R.drawable.ic_close, { super.onBackPressed() })
            )
        )
    }

    override fun initComponents() {
        val layout: ViewGroup = activityMusicQueue

        val currentMusic: Music = playlistController.getCurrentMusic()

        val musicTitle = layout.textMusicTitle
        val musicLabel = layout.textMusicLabel
        val playlistName = layout.textPlaylistName
        val recyclerMusicList = layout.recyclerNextFromQueue

        musicTitle.text = currentMusic.title
        musicLabel.text = TextUtils.getMusicLabel(currentMusic.artist, currentMusic.album)
        playlistName.text = intent.getStringExtra(EXTRA_PLAYLIST_NAME)

        buildQueue(recyclerMusicList)
    }

    private fun buildQueue(recyclerMusicList: RecyclerView) {
        val list = playlistController.musicQueueRunning

        with(recyclerMusicList) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                this@MusicQueueActivity,
                RecyclerView.VERTICAL,
                false
            )
            setHasFixedSize(true)
            adapter = MusicQueueAdapter(list) { music -> }
        }
    }

    companion object {
        private const val EXTRA_PLAYLIST_NAME = "EXTRA_PLAYLIST_NAME"

        fun getStartIntent(
            context: Context,
            playlist: String?
        ): Intent {
            return Intent(context, MusicQueueActivity::class.java).apply {
                putExtra(EXTRA_PLAYLIST_NAME, playlist)
            }
        }
    }
}
