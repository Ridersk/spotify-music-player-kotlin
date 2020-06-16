package com.spotifyclone.data.model

data class QueueHeader(
    val title: String,
    val subtitle: String = ""
) : QueueItem
