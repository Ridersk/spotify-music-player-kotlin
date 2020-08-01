package com.spotifyclone.tools.musicplayer

import androidx.lifecycle.Observer
import com.spotifyclone.data.model.Music
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PlaylistObserverProvider : Observer<List<Music>> {

    private val receivers = mutableListOf<MusicObserver>()

    override fun onChanged(musics: List<Music>?) {
        musics?.let {
            addListToReceivers(musics)
        }
    }

    fun addReceiver(observer: MusicObserver) {
        this.receivers.add(observer)
    }

    private fun addListToReceivers(musics: List<Music>) {
        runBlocking {
            launch(Dispatchers.Default) {
                receivers.forEach { receiver ->
                    receiver.updatedList(musics)
                }
            }
        }
    }
}
