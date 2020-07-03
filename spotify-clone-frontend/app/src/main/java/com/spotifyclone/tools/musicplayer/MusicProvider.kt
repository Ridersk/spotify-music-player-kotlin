package com.spotifyclone.tools.musicplayer

import com.spotifyclone.data.model.Music

interface MusicProvider {
    fun addMusicObserver(observer: MusicObserver)
    fun removeMusicObserver(observer: MusicObserver)
    fun alertChangedMusic(music: Music)
}