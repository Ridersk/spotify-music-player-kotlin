package com.spotifyclone.tools.musicplayer

import android.content.Context
import com.spotifyclone.data.model.Music
import com.spotifyclone.tools.basepatterns.SingletonHolder

class PlaylistController private constructor(var context: Context): PlaylistObserverReceiver<Music> {

    private var musicList = mutableListOf<Music>()
    private var musicPlaying = 0

    override fun receiverList(list: List<Music>) {
        this.musicList = list.toMutableList()
    }

    override fun chooseItem(index: Int) {
        this.musicPlaying = index
    }

    fun nextMusic() {
    }

    fun previousMusic() {
    }

    fun addMusicToPlaylist(music: Music) {
        this.musicList.add(music)
    }

    fun removeMusicFromPlaylist(index: Int) {
        this.musicList.removeAt(index)
    }


    companion object: SingletonHolder<PlaylistController, Context>(::PlaylistController)
}