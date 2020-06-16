package com.spotifyclone.data.model

data class QueueMusic(
    var position: Int,
    var checked: Boolean,
    val music: Music
): QueueItem
