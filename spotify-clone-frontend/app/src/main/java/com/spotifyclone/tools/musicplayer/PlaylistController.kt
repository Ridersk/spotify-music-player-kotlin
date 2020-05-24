package com.spotifyclone.tools.musicplayer

import android.content.Context
import com.spotifyclone.data.model.Music
import com.spotifyclone.tools.basepatterns.SingletonHolder

class PlaylistController private constructor(var context: Context): PlaylistObserverReceiver<Music> {

    private var musicList = mutableListOf<Music>()
    private var musicPositionPlaying = 0

    override fun receiverList(list: List<Music>) {
        this.musicList = list.toMutableList()
    }

    override fun chooseItem(position: Int) {
        this.musicPositionPlaying = position
    }

    fun nextMusic() {
        val position = (musicPositionPlaying + 1) % musicList.size
        chooseItem(position)
    }

    fun previousMusic() {
        val position = (musicPositionPlaying - 1) % musicList.size
        chooseItem(position)
    }

    fun getCurrentMusic(): Music {
        return if (musicPositionPlaying >= 0 && musicPositionPlaying < musicList.size) {
            musicList[musicPositionPlaying]
        } else {
            Music()
        }
    }

    fun addMusicToPlaylist(music: Music) {
        this.musicList.add(music)
    }

    fun removeMusicFromPlaylist(index: Int) {
        this.musicList.removeAt(index)
    }


    companion object: SingletonHolder<PlaylistController, Context>(::PlaylistController)
}