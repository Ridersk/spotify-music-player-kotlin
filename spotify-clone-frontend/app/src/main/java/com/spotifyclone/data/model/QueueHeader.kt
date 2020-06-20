package com.spotifyclone.data.model

data class QueueHeader(
    val title: String,
    val subtitle: String = "",
    val type: Int
) : QueueItem {
    companion object {
        const val TYPE_NORMAL_QUEUE = 0
        const val TYPE_PRIORITY_QUEUE = 1
    }
}
