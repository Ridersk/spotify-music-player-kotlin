package com.spotifyclone.presentation.musicqueue

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.tools.musicplayer.MusicObserver
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import com.spotifyclone.tools.utils.TextUtils
import kotlinx.android.synthetic.main.activity_music_queue.*
import kotlinx.android.synthetic.main.activity_music_queue.view.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.util.*

class MusicQueueActivity : BaseActivity(), MusicObserver {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(this@MusicQueueActivity)

    init {
        playlistMusicPlayer.addMusicObserver(this)
    }

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

    override fun changedMusic(music: Music) {
        reloadActivity(
            music.albumUriId,
            intent.getStringExtra(EXTRA_PLAYLIST_NAME)
        )
    }

    override fun initComponents() {
        val layout: ViewGroup = activityMusicQueue

        val currentMusic: Music = playlistMusicPlayer.getCurrentMusic()

        val musicTitle = layout.textMusicTitle
        val musicLabel = layout.textMusicLabel
        val imageAlbum = layout.imageAlbum
        val playlistName = layout.textPlaylistName
        val recyclerMusicList = layout.recyclerNextFromQueue

        musicTitle.text = currentMusic.title
        musicLabel.text = TextUtils.getMusicLabel(currentMusic.artist, currentMusic.album)
        insertAlbumArt(imageAlbum, intent.getLongExtra(EXTRA_ALBUM_URI_ID, -1))
        playlistName.text = intent.getStringExtra(EXTRA_PLAYLIST_NAME)

        buildQueue(recyclerMusicList)
    }

    private fun reloadActivity(albumUriId: Long, playlist: String? = "") {
        intent?.apply {
            putExtra(EXTRA_PLAYLIST_NAME, playlist)
            putExtra(EXTRA_ALBUM_URI_ID, albumUriId)
        }

        initComponents()
    }

    private fun buildQueue(recyclerMusicList: RecyclerView) {
        val list = playlistMusicPlayer.musicQueueRunning
        val scopedList = list.subList(playlistMusicPlayer.positionPlaying + 1, list.size)

        val musicQueueAdapter = MusicQueueAdapter(scopedList)

        with(recyclerMusicList) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                this@MusicQueueActivity,
                RecyclerView.VERTICAL,
                false
            )
            setHasFixedSize(true)
            adapter = musicQueueAdapter
        }


        val touchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

            override fun onMove(
                recyclerView: RecyclerView,
                dragged: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val draggedPosition = dragged.adapterPosition
                val targetPosition = target.adapterPosition

                Collections.swap(list, draggedPosition, targetPosition)

                musicQueueAdapter.notifyItemMoved(draggedPosition, targetPosition)

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        touchHelper.attachToRecyclerView(recyclerMusicList)
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
