package com.spotifyclone.tools.musicplayer

import com.spotifyclone.data.model.Music

interface MusicProvider {
    fun addObserver(observer: MusicObserver)
    fun alertChoosedMusic(music: Music)
}