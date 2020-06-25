package com.spotifyclone.data.repository

import com.spotifyclone.data.MusicsResult

interface PlaylistMusicsRepository {

    fun getMusics(resultCallback: (result: MusicsResult) -> Unit)
}