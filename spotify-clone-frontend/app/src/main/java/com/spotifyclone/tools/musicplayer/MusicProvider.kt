package com.spotifyclone.tools.musicplayer

import com.spotifyclone.data.model.Music
import java.util.*

interface MusicProvider {
    fun addMusicObserver(observer: MusicObserver) {}
    fun removeMusicObserver(observer: MusicObserver) {}
    fun notifyChangedMusic(music: Music) {}
    fun notifyUpdateList(musicList: List<Music>) {}
    fun chooseMusic(id: UUID) {}
}
