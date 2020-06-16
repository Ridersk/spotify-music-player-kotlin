package com.spotifyclone.presentation.musicqueue

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.data.model.QueueHeader
import com.spotifyclone.data.model.QueueItem
import com.spotifyclone.data.model.QueueMusic
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.tools.musicplayer.MusicObserver
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import com.spotifyclone.tools.utils.TextUtils
import kotlinx.android.synthetic.main.activity_music_queue.*
import kotlinx.android.synthetic.main.activity_music_queue.view.*
import kotlinx.android.synthetic.main.dialog_small.*
import kotlinx.android.synthetic.main.dialog_small.view.*
import kotlinx.android.synthetic.main.include_toolbar.*

import java.util.*

class MusicQueueActivity : BaseActivity(), MusicObserver {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(this@MusicQueueActivity)
    private lateinit var dialog: View
    private var selectedMusics = mutableListOf<QueueMusic>()
    private var scopedList = mutableListOf<QueueItem>()
    private lateinit var musicQueueAdapter: MusicQueueAdapter

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
        val recyclerMusicList = layout.recyclerNextFromQueue

        musicTitle.text = currentMusic.title
        musicLabel.text = TextUtils.getMusicLabel(currentMusic.artist, currentMusic.album)
        insertAlbumArt(imageAlbum, intent.getLongExtra(EXTRA_ALBUM_URI_ID, -1))

        createDialog()
        buildQueue(recyclerMusicList)
    }

    private fun reloadActivity(albumUriId: Long, playlist: String? = "") {
        intent?.apply {
            putExtra(EXTRA_PLAYLIST_NAME, playlist)
            putExtra(EXTRA_ALBUM_URI_ID, albumUriId)
        }

        initComponents()
    }

    private fun createDialog() {
        dialog = dialogSmall
        val addButton = dialog.btnAdd
        val removeButton = dialog.btnRemove

        addButton.setOnClickListener {
        }

        removeButton.setOnClickListener {
            this.removeMusics()
        }

        dialog.visibility = View.GONE

    }

    private fun buildQueue(recyclerMusicList: RecyclerView) {
        val musicList: MutableList<Music> = playlistMusicPlayer.musicQueueRunning
        val musicQueueList: MutableList<QueueMusic> =
            MutableList(musicList.size) { index ->
                QueueMusic(
                    position = index,
                    checked = false,
                    music = musicList[index]
                )
            }

        scopedList =
            mutableListOf(
                QueueHeader(
                    getString(R.string.music_queue_txt_next_from),
                    intent.getStringExtra(EXTRA_PLAYLIST_NAME)!!
                )
            )
        scopedList.addAll(
            musicQueueList.subList(
                playlistMusicPlayer.positionPlaying + MUSIC_OUTSIDE_DISPLAY_QUEUE,
                musicQueueList.size
            )
        )

        musicQueueAdapter = MusicQueueAdapter(
            items = scopedList,
            onItemClickListener = {},
            onCheckboxClickListener = { musicItem -> handleSelectMusic(musicItem) }
        )

        with(recyclerMusicList) {
            layoutManager = LinearLayoutManager(
                this@MusicQueueActivity,
                RecyclerView.VERTICAL,
                false
            )
            adapter = musicQueueAdapter
        }


        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            var nextFromQueuePosition = 0
            var nextInQueueSize = 0
            var qttHeaders = 1

            override fun onMove(
                recyclerView: RecyclerView,
                dragged: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val draggedPosition = dragged.adapterPosition
                val targetPosition = target.adapterPosition

                if (scopedList[draggedPosition] is QueueMusic) {

                    if (scopedList[targetPosition] is QueueMusic) {
                        val groupHeader: Int = if (nextFromQueuePosition == 0) {
                            1
                        } else {
                            if (targetPosition > nextFromQueuePosition) {
                                2
                            } else {
                                1
                            }
                        }
                        val gap:Int = MUSIC_OUTSIDE_DISPLAY_QUEUE - groupHeader

                        Collections.swap(
                            musicList,
                            draggedPosition + gap,
                            targetPosition + gap
                        )
                    }

                    Collections.swap(
                        scopedList,
                        draggedPosition,
                        targetPosition
                    )
                    musicQueueAdapter.notifyItemMoved(draggedPosition, targetPosition)
                }

                if (targetPosition < draggedPosition && targetPosition == nextFromQueuePosition) {
                    nextFromQueuePosition++
                    nextInQueueSize++
                } else if (targetPosition > draggedPosition && targetPosition == nextFromQueuePosition) {
                    nextFromQueuePosition--
                    nextInQueueSize--
                }

                return false
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                if (scopedList[0] is QueueMusic) {
                    scopedList.add(
                        0,
                        QueueHeader(getString(R.string.music_queue_txt_next_in_queue))
                    )
                    musicQueueAdapter.notifyDataSetChanged()
                    nextInQueueSize += 1
                    nextFromQueuePosition += 1
                    qttHeaders += 1
                }

                if (nextInQueueSize == 1 && scopedList[0] is QueueHeader) {
                    scopedList.removeAt(0)
                    musicQueueAdapter.notifyDataSetChanged()
                    nextInQueueSize = 0
                    nextFromQueuePosition = 0
                    qttHeaders -= 1
                }
                super.clearView(recyclerView, viewHolder)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }
        })

        touchHelper.attachToRecyclerView(recyclerMusicList)
    }

    private fun removeMusics() {
        scopedList.removeAll(selectedMusics)
        resetCheckboxes()


        playlistMusicPlayer.removeMusics(
            selectedMusics.map { selected -> selected.music }.toMutableList()
        )

        selectedMusics = mutableListOf()
        musicQueueAdapter.notifyDataSetChanged()
        toogleDialog()
    }

    private fun addMusics() {
        scopedList

    }

    private fun resetCheckboxes() {
        scopedList.forEach { item -> if (item is QueueMusic) item.checked = false }
    }

    private fun handleSelectMusic(music: QueueMusic) {
        val wasEmpty = selectedMusics.isEmpty()

        if (!selectedMusics.contains(music)) {
            selectedMusics.add(music)
        } else {
            selectedMusics.remove(music)
        }

        if (wasEmpty != selectedMusics.isEmpty()) {
            toogleDialog()
        }
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

        private const val MUSIC_OUTSIDE_DISPLAY_QUEUE = 1

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
