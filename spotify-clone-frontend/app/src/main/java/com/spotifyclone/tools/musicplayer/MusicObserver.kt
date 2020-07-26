package com.spotifyclone.tools.musicplayer

import com.spotifyclone.data.model.Music

interface MusicObserver {
    fun changedMusic(music: Music) {}
    fun updatedList(newMusicList: List<Music>) {}
}
