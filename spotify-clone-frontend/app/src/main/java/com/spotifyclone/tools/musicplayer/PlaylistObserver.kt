package com.spotifyclone.tools.musicplayer

import java.util.*

interface PlaylistObserver<T> {
    fun receiverList(list: List<T>)
    fun chooseMusic(id: UUID)
}