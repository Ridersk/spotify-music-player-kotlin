package com.spotifyclone.tools.musicplayer

interface MusicProvider {
    fun addObserver(observer: MusicObserver)
    fun alertChoosedMusic()
}