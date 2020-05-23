package com.spotifyclone.tools.musicplayer

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.spotifyclone.data.model.Music
import com.spotifyclone.presentation.music.MusicPlayerActivity
import com.spotifyclone.presentation.playlist.PlaylistMusicsAdapter

class PlaylistObserverProvider(
    private val componentList: RecyclerView,
    private val playlistName: String = ""
): Observer<List<Music>> {

    private val receivers = mutableListOf<PlaylistObserverReceiver<Music>>()

    override fun onChanged(musics: List<Music>?) {
        musics?.let {
            with(componentList) {
                layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                    context,
                    RecyclerView.VERTICAL,
                    false
                )
                setHasFixedSize(true)
                adapter =
                    PlaylistMusicsAdapter(musics) { music ->
                        val intent =
                            MusicPlayerActivity.getStartIntent(
                                context,
                                music.name,
                                music.artist,
                                music.contentUriId,
                                music.albumUriId,
                                playlistName
                            )

                        alertChoosedMusic(music.index)
                        context.startActivity(intent)
                    }
            }
            addListToReceivers(musics)
        }
    }

    fun addReceiver(receiver: PlaylistObserverReceiver<Music>) {
        this.receivers.add(receiver)
    }

    private fun addListToReceivers(musics: List<Music>) {
        for (receiver in receivers) {
            receiver.receiverList(musics)
        }
    }

    private fun alertChoosedMusic(index: Int) {
        for (receiver in receivers) {
            receiver.chooseItem(index)
        }
    }
}