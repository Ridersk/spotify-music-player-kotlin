package com.spotifyclone.presentation.musicqueue

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.data.model.QueueHeader
import com.spotifyclone.data.model.QueueItem
import com.spotifyclone.data.model.QueueMusic
import com.spotifyclone.tools.musicplayer.MusicObserver
import com.spotifyclone.tools.musicplayer.MusicProvider
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import com.spotifyclone.tools.musicqueue.MusicQueueController
import com.spotifyclone.tools.utils.ListUtils
import java.util.*

class MusicQueueView(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val playlistName: String? = "",
    private val callbackSelectedItems: () -> Unit = {}
) : MusicObserver, MusicProvider {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(context)
    private var selectedMusics = mutableListOf<QueueMusic>()
    private val itemQueue = mutableListOf<QueueItem>()
    private lateinit var musicQueueAdapter: MusicQueueAdapter
    private lateinit var musicQueueController: MusicQueueController

    fun create() {
        itemQueue.addAll(
            buildRecyclerQueue(
                playlistMusicPlayer.normalMusicQueueRunning,
                playlistMusicPlayer.priorityMusicQueue
            )
        )

        musicQueueAdapter = MusicQueueAdapter(
            context,
            items = itemQueue,
            onItemClickListener = { musicItem ->
                if (musicItem is QueueMusic) chooseMusic(musicItem.music.id)
            },
            onCheckboxClickListener = { musicItem -> handleSelectMusic(musicItem) }
        )

        with(recyclerView) {
            layoutManager = LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            )
            adapter = musicQueueAdapter
        }

        musicQueueController = createCallback()
        ItemTouchHelper(musicQueueController).attachToRecyclerView(recyclerView)
    }

    fun update() {
        itemQueue.clear()
        itemQueue.addAll(
            buildRecyclerQueue(
                playlistMusicPlayer.normalMusicQueueRunning,
                playlistMusicPlayer.priorityMusicQueue
            )
        )

        musicQueueAdapter.notifyDataSetChanged()
    }

    override fun updatedList(newMusicList: List<Music>) {}

    override fun chooseMusic(id: UUID) {
        playlistMusicPlayer.chooseMusic(id)
        musicQueueAdapter.notifyDataSetChanged()
    }

    private fun createCallback(): MusicQueueController {
        return MusicQueueController(
            context,
            musicQueueAdapter,
            itemQueue
        ) { priorityHeaderPos, normalHeaderPos ->
            updateQueues(
                priorityHeaderPos,
                normalHeaderPos
            )
        }
    }

    private fun updateQueues(priorityHeaderPos: Int, normalHeaderPos: Int) {
        val priorityList: List<Music> = if (normalHeaderPos > 0) {
            ListUtils.sublist(itemQueue, priorityHeaderPos + 1, normalHeaderPos)
                .map { item -> (item as QueueMusic).music }
        } else listOf()
        val normalList: List<Music> =
            ListUtils.sublist(itemQueue, normalHeaderPos + 1)
                .map { item -> (item as QueueMusic).music }
        val playingRealList = playlistMusicPlayer.positionPlaying

        playlistMusicPlayer.recreatePriorityMusicQueue(priorityList)
        playlistMusicPlayer.updateMusicsFromNormalList(normalList, start = playingRealList + 1)
    }

    private fun buildRecyclerQueue(
        normalList: MutableList<Music>,
        priorityList: MutableList<Music> = mutableListOf()
    ): MutableList<QueueItem> {
        val priorityQueue: MutableList<QueueItem> = convertMusicListToItemQueue(
            list = priorityList,
            headerTitle = context.getString(R.string.music_queue_next_in_queue),
            type = QueueHeader.TYPE_PRIORITY_QUEUE
        )

        val normalQueue: MutableList<QueueItem> = convertMusicListToItemQueue(
            list = ListUtils.sublist(
                normalList,
                playlistMusicPlayer.firstNotPlayedFromNormalQueue()
            ),
            headerTitle = context.getString(R.string.music_queue_next_from),
            headerSubtitle = playlistName,
            type = QueueHeader.TYPE_NORMAL_QUEUE
        )

        val finalItemQueue: MutableList<QueueItem> = mutableListOf()
        finalItemQueue.addAll(priorityQueue)
        finalItemQueue.addAll(normalQueue)

        return finalItemQueue
    }

    private fun convertMusicListToItemQueue(
        list: List<Music> = listOf(),
        headerTitle: String, headerSubtitle: String? = "", type: Int
    ): MutableList<QueueItem> {
        if (list.isNotEmpty()) {
            val itemQueue: MutableList<QueueItem> = mutableListOf(
                QueueHeader(
                    title = headerTitle,
                    subtitle = headerSubtitle ?: "",
                    type = type
                )
            )

            itemQueue.addAll(MutableList(list.size) { index ->
                QueueMusic(
                    position = index,
                    checked = false,
                    music = list[index].deepCopy()
                )
            })
            return itemQueue
        }
        return mutableListOf()
    }

    fun addMusics() {
        if (this.selectedMusics.isNotEmpty()) {
            musicQueueController.addToPriorityQueue(selectedMusics)
            resetSelecteds()
        }
    }

    fun removeMusics() {
        if (selectedMusics.isNotEmpty()) {
            musicQueueController.removeMusics(selectedMusics)
            resetCheckboxes()
            resetSelecteds()
        }
    }

    private fun resetSelecteds() {
        selectedMusics = mutableListOf()
        musicQueueAdapter.notifyDataSetChanged()
        callbackSelectedItems.invoke()
    }

    private fun resetCheckboxes() {
        itemQueue.forEach { item -> if (item is QueueMusic) item.checked = false }
    }

    private fun handleSelectMusic(music: QueueMusic) {
        val wasEmpty = selectedMusics.isEmpty()

        if (!selectedMusics.contains(music)) {
            music.checked = true
            selectedMusics.add(music)
        } else {
            music.checked = false
            selectedMusics.remove(music)
        }

        if (wasEmpty != selectedMusics.isEmpty()) {
            callbackSelectedItems.invoke()
        }
    }

}
