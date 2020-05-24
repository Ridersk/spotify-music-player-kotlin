package com.spotifyclone.tools.musicplayer

interface PlaylistObserver<T> {
    fun receiverList(list: List<T>)
    fun chooseItem(position: Int)
}