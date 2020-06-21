package com.spotifyclone.tools.musicqueue

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.data.model.QueueHeader
import com.spotifyclone.data.model.QueueItem
import com.spotifyclone.data.model.QueueMusic
import com.spotifyclone.presentation.musicqueue.MusicQueueAdapter
import com.spotifyclone.tools.utils.ListUtils
import java.util.*

class MusicQueueController(
    private val context: Context,
    private val musicQueueAdapter: MusicQueueAdapter,
    private val itemQueue: MutableList<QueueItem>,
    private val callbackChangedList: (priorityHeaderPos: Int, normalHeaderPos: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

    private val priorityListHeaderPosition: Int = 0
    private var normalListHeaderPosition: Int = 0

    override fun onMove(
        recyclerView: RecyclerView,
        dragged: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val draggedPosition = dragged.adapterPosition
        val targetPosition = target.adapterPosition

        Collections.swap(
            itemQueue,
            draggedPosition,
            targetPosition
        )
        musicQueueAdapter.notifyItemMoved(draggedPosition, targetPosition)

        if (targetPosition < draggedPosition && targetPosition == normalListHeaderPosition) {
            normalListHeaderPosition++
        } else if (targetPosition > draggedPosition && targetPosition == normalListHeaderPosition) {
            normalListHeaderPosition--
        }

        return false
    }

    override fun canDropOver(
        recyclerView: RecyclerView,
        current: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val draggedPosition = current.adapterPosition
        val targetPosition = target.adapterPosition
        return itemQueue[draggedPosition] is QueueMusic &&
                (itemQueue[targetPosition] is QueueMusic ||
                        (itemQueue[targetPosition] as QueueHeader)
                            .type != QueueHeader.TYPE_PRIORITY_QUEUE)
    }

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        updateQueue()
        super.clearView(recyclerView, viewHolder)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    fun addToPriorityQueue(musics: List<QueueMusic>) {
        val newMusics = deepCopyList(musics)
        itemQueue.addAll(normalListHeaderPosition, newMusics)

        this.normalListHeaderPosition += newMusics.size
        updateQueue()
    }

    private fun deepCopyList(musics: List<QueueMusic>): List<QueueMusic> {
        val newList = mutableListOf<QueueMusic>()
        musics.forEach { musicItem ->
            newList.add(musicItem.deepCopy())
        }
        return newList
    }

    fun removeMusics(musics: List<QueueMusic>) {
        itemQueue.removeAll(musics)
        for (index in 0..itemQueue.size) {
            if (itemQueue[index] is QueueHeader &&
                (itemQueue[index] as QueueHeader).type == QueueHeader.TYPE_NORMAL_QUEUE
            ) {
                this.normalListHeaderPosition = index
                break
            }
        }
        updateQueue()
    }

    private fun updateQueue() {
        if (itemQueue[0] is QueueMusic) {
            itemQueue.add(
                0,
                QueueHeader(
                    context.getString(R.string.music_queue_txt_next_in_queue),
                    type = QueueHeader.TYPE_PRIORITY_QUEUE
                )
            )
            musicQueueAdapter.notifyDataSetChanged()
            normalListHeaderPosition += 1
        }

        if (normalListHeaderPosition == 1) {
            itemQueue.removeAt(0)
            normalListHeaderPosition = 0
            musicQueueAdapter.notifyDataSetChanged()
        }
        callbackChangedList.invoke(priorityListHeaderPosition, normalListHeaderPosition)
    }
}