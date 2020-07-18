package com.spotifyclone.presentation.playlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.tools.animations.ReducerAndRegain
import com.spotifyclone.tools.utils.TextUtils
import com.spotifyclone.tools.utils.ViewUtils
import kotlinx.android.synthetic.main.item_music.view.*
import java.util.*

open class PlaylistMusicsAdapter(
    private val context: Context,
    private val musics: List<Music>,
    private val onItemClickListener: ((music: Music) -> Unit) = {}
) : RecyclerView.Adapter<PlaylistMusicsAdapter.ViewHolder>() {
    private lateinit var selectedUUID: UUID

    init {
        super.setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_music, parent, false
        )

        return ViewHolder(context, itemView, onItemClickListener)
    }

    override fun getItemCount(): Int = musics.count()

    override fun getItemId(position: Int): Long {
        return super.getItemId(position).hashCode().toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (this::selectedUUID.isInitialized && musics[position].id == selectedUUID) {
            holder.bindView(position, musics[position], true)
        } else {
            holder.bindView(position, musics[position])
        }
    }

    fun select(id: UUID) {
        this.selectedUUID = id
    }

    inner class ViewHolder(
        private val context: Context,
        itemView: View,
        private val onItemClickListener: (music: Music) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.textMusicTitle
        private val musiclabel = itemView.textMusicLabel

        fun bindView(position: Int, music: Music, selected: Boolean = false) {
            title.text = music.title
            musiclabel.text = TextUtils.getMusicLabel(music.artist, music.album)

            itemView.setOnClickListener {
                onItemClickListener.invoke(music)
            }

            itemView.setOnTouchListener { view, event ->
                ReducerAndRegain(context).onTouch(
                    view,
                    event
                )
            }

            if (position == 0) {
                ViewUtils.setTopMargin(itemView, 100)
            }

            if (selected) title.setTextColor(ContextCompat.getColor(context, R.color.green))
            else title.setTextColor(ContextCompat.getColor(context, R.color.white))
        }

    }

}
