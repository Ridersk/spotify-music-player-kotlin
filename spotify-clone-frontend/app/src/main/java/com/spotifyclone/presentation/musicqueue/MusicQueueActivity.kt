package com.spotifyclone.presentation.musicqueue

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.tools.musicplayer.MusicObserver
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import com.spotifyclone.tools.utils.ImageUtils
import com.spotifyclone.tools.utils.TextUtils
import kotlinx.android.synthetic.main.activity_music_queue.*
import kotlinx.android.synthetic.main.activity_music_queue.view.*
import kotlinx.android.synthetic.main.fragment_two_options.*
import kotlinx.android.synthetic.main.fragment_two_options.view.*
import kotlinx.android.synthetic.main.include_toolbar.*

class MusicQueueActivity : BaseActivity(), MusicObserver {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(this@MusicQueueActivity)
    private lateinit var dialog: View
    private lateinit var musicQueueView: MusicQueueView

    init {
        playlistMusicPlayer.addMusicObserver(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_music_queue)

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
            ImageUtils.insertBitmapInView(
                applicationContext,
                imageAlbum,
                intent.getLongExtra(EXTRA_ALBUM_URI_ID, -1)
            )
        }
        createDialog({ musicQueueView.addMusics() }, { musicQueueView.removeMusics() })
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
            toogleDialog()
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

    private fun createDialog(funcAddMusic: () -> Unit, funcRemoveMusic: () -> Unit) {
        dialog = dialogBottom
        val addButton = dialog.btnAdd
        val removeButton = dialog.btnRemove

        addButton.text = getString(R.string.music_queue_fragment_add_music)
        removeButton.text = getString(R.string.music_queue_fragment_remove_music)

        addButton.setOnClickListener {
            funcAddMusic.invoke()
        }

        removeButton.setOnClickListener {
            funcRemoveMusic.invoke()
        }

        dialog.visibility = View.GONE

    }

    private fun toogleDialog() {
        if (dialog.visibility == View.GONE) {
            dialog.visibility = View.VISIBLE
        } else {
            dialog.visibility = View.GONE
        }
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
