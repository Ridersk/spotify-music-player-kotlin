package com.spotifyclone.presentation.playlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.tools.utils.TextUtils
import kotlinx.android.synthetic.main.item_music.view.*
import kotlin.coroutines.coroutineContext

open class PlaylistMusicsAdapter(
    private val context: Context,
    private val musics: List<Music>,
    private val onItemClickListener: ((music: Music) -> Unit) = {}
) : RecyclerView.Adapter<PlaylistMusicsAdapter.ViewHolder>() {
    private var selected = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_music, parent, false
        )

        return ViewHolder(context, itemView, onItemClickListener)
    }

    override fun getItemCount(): Int = musics.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position ==  selected) {
            holder.bindView(musics[position], true)
        } else {
            holder.bindView(musics[position])
        }
    }

    fun select(position: Int) {
        this.selected = position
    }

    class ViewHolder(
        private val context: Context,
        itemView: View,
        private val onItemClickListener: (music: Music) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.textMusicTitle
        private val musiclabel = itemView.textMusicLabel

        fun bindView(music: Music, selected:Boolean = false) {
            title.text = music.title
            if (selected) {
                title.setTextColor(ContextCompat.getColor(context, R.color.green))
            }
            musiclabel.text = TextUtils.getMusicLabel(music.artist, music.album)

            itemView.setOnClickListener {
                onItemClickListener.invoke(music)
            }
        }
    }

}
