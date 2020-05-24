package com.spotifyclone.tools.musicplayer

import com.spotifyclone.data.model.Music

interface MusicObserver {
    fun chooseMusic(music: Music)
}