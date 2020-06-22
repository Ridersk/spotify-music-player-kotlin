package com.spotifyclone.tools.musicplayer

import androidx.lifecycle.Observer
import com.spotifyclone.data.model.Music

class PlaylistObserverProvider : Observer<List<Music>> {

    private val receivers = mutableListOf<PlaylistObserver<Music>>()

    override fun onChanged(musics: List<Music>?) {
        musics?.let {
            addListToReceivers(musics)
        }
    }

    fun addReceiver(observer: PlaylistObserver<Music>) {
        this.receivers.add(observer)
    }

    private fun addListToReceivers(musics: List<Music>) {
        for (receiver in receivers) {
            receiver.receiverList(musics)
        }
    }
}