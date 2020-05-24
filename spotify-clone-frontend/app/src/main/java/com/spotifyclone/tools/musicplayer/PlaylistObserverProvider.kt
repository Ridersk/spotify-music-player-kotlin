package com.spotifyclone.tools.musicplayer

import androidx.lifecycle.Observer
import com.spotifyclone.data.model.Music

class PlaylistObserverProvider : Observer<List<Music>> {

    private val receivers = mutableListOf<PlaylistObserverReceiver<Music>>()

    override fun onChanged(musics: List<Music>?) {
        musics?.let {
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
}