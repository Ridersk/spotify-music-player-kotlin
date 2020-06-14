package com.spotifyclone.presentation.musicqueue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.tools.utils.TextUtils
import kotlinx.android.synthetic.main.item_queue_music.view.textMusicTitle
import kotlinx.android.synthetic.main.item_queue_music.view.textMusicLabel

class MusicQueueAdapter(
    private val musics: List<Music>,
    private val onItemClickListener: ((music: Music) -> Unit) = {}
): RecyclerView.Adapter<MusicQueueAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_queue_music, parent, false
        )

        return ViewHolder(itemView, onItemClickListener)
    }

    override fun getItemCount(): Int = musics.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(musics[position])
    }


    class ViewHolder(
        itemView: View,
        private val onItemClickListener: (music: Music) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.textMusicTitle
        private val musiclabel = itemView.textMusicLabel

        fun bindView(music: Music) {
            title.text = music.title
            musiclabel.text = TextUtils.getMusicLabel(music.artist, music.album)

            itemView.setOnClickListener {
                onItemClickListener.invoke(music)
            }
        }
    }

}
