package com.spotifyclone.tools.musicplayer

interface PlaylistObserverReceiver<T> {
    fun receiverList(list: List<T>)
    fun chooseItem(position: Int)
}