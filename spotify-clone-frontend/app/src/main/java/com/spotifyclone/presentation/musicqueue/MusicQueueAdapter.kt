package com.spotifyclone.presentation.musicqueue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spotifyclone.R
import com.spotifyclone.data.model.QueueHeader
import com.spotifyclone.data.model.Music
import com.spotifyclone.data.model.QueueItem
import com.spotifyclone.data.model.QueueMusic
import com.spotifyclone.tools.utils.TextUtils
import kotlinx.android.synthetic.main.header_queue.view.*
import kotlinx.android.synthetic.main.item_queue_music.view.*

class MusicQueueAdapter(
    private val items: List<QueueItem>,
    private val onItemClickListener: ((music: Music) -> Unit) = {},
    private val onCheckboxClickListener: ((music: QueueMusic) -> Unit)
) : RecyclerView.Adapter<MusicQueueAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == TYPE_HEADER) {
            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.header_queue, parent, false
            )

            return ViewHolderHeader(itemView)
        }

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_queue_music, parent, false
        )

        return ViewHolderItem(itemView, onItemClickListener, onCheckboxClickListener)
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemBind = items[position]
        if (holder is ViewHolderItem && itemBind is QueueMusic) {
            holder.bindView(itemBind)
        } else if (holder is ViewHolderHeader && itemBind is QueueHeader) {
            holder.bindView(itemBind)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (items[position] is QueueHeader)
            return TYPE_HEADER
        else if (items[position] is QueueHeader)
            return TYPE_ITEM
        return TYPE_ERROR
    }

    open class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView)

    class ViewHolderItem(
        itemView: View,
        private val onItemClickListener: ((music: Music) -> Unit) = {},
        private val onCheckboxClickListener: (music: QueueMusic) -> Unit
    ) : ViewHolder(itemView) {
        private val title = itemView.textMusicTitle
        private val musiclabel = itemView.textMusicLabel
        private val checkbox = itemView.checkboxMusic

        fun bindView(music: QueueMusic) {
            title.text = music.music.title
            musiclabel.text = TextUtils.getMusicLabel(music.music.artist, music.music.album)

            itemView.setOnClickListener {
                onItemClickListener.invoke(music.music)
            }

            checkbox.isChecked = music.checked
            checkbox.setOnClickListener {
                onCheckboxClickListener.invoke(music)
            }
        }
    }

    class ViewHolderHeader(
        itemView: View
    ) : ViewHolder(itemView) {
        private val title = itemView.txtHeaderQueueTitle
        private val subtitle = itemView.txtHeaderQueuePlaylist

        fun bindView(headeItem: QueueHeader) {
            title.text = headeItem.title
            subtitle.text = headeItem.subtitle
        }
    }

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_HEADER = 1
        private const val TYPE_ERROR = 3
    }
}
