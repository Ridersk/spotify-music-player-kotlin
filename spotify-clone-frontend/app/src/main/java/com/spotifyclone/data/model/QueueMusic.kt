package com.spotifyclone.data.model

import com.google.gson.Gson

data class QueueMusic(
    var position: Int,
    var checked: Boolean,
    val music: Music
): QueueItem {
    fun deepCopy(): QueueMusic {
        val json = Gson().toJson(this)
        return Gson().fromJson(json, QueueMusic::class.java)
    }
}
