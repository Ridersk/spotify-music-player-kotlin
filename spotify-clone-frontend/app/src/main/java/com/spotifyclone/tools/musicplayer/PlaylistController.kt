package com.spotifyclone.tools.musicplayer

import android.content.Context
import com.spotifyclone.data.model.Music
import com.spotifyclone.tools.basepatterns.SingletonHolder

class PlaylistController private constructor(var context: Context) : PlaylistObserver<Music>,
    MusicProvider {

    private var musicList = mutableListOf<Music>()
    private var musicPositionPlaying = 0
    private var observers = mutableListOf<MusicObserver>()
    var cycleAll = false
        private set
    var cycleOne = false
        private set
    var cycleMode = listOf<Int>()
    var random = false
        private set

    override fun receiverList(list: List<Music>) {
        this.musicList = list.toMutableList()
    }

    override fun chooseItem(position: Int) {
        this.musicPositionPlaying = position
        alertChoosedMusic()
    }

    override fun addObserver(observer: MusicObserver) {
        this.observers.add(observer)
    }

    override fun alertChoosedMusic() {
        val music = getCurrentMusic()
        for (observer in this.observers) {
            observer.chooseMusic(music)
        }
    }

    fun nextMusic() {
        val position: Int = if (musicPositionPlaying < musicList.size -1 || cycleAll)
            (musicPositionPlaying + 1) % musicList.size
        else musicPositionPlaying
        chooseItem(position)
    }

    fun previousMusic() {
        var position: Int = (musicPositionPlaying - 1) % musicList.size

        if (position < 0) {
            position = if (cycleAll) musicList.size - 1 else 0
        }
        chooseItem(position)
    }

    fun getCurrentMusic(): Music {
        return if (musicPositionPlaying >= 0 && musicPositionPlaying < musicList.size) {
            musicList[musicPositionPlaying]
        } else {
            Music()
        }
    }

    fun toogleModeCycle() {
        this.cycleAll = !this.cycleAll
    }

    fun toogleRandom() {
        this.random = !this.random

        if (this.random) shuffleList()
    }

    private fun shuffleList() {
        if (random) {
            this.musicList.shuffle()
        }
    }

    fun addMusicToPlaylist(music: Music) {
        this.musicList.add(music)
    }

    fun removeMusicFromPlaylist(index: Int) {
        this.musicList.removeAt(index)
    }


    companion object : SingletonHolder<PlaylistController, Context>(::PlaylistController)
}