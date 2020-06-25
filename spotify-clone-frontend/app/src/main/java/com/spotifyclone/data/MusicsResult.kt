package com.spotifyclone.data

import com.spotifyclone.data.model.Music

sealed class MusicsResult {
    class Success(val musics: List<Music>) : MusicsResult()
    class ApiError(val statusCode: Int, val warningResId: Int?, val descriptionResId: Int? = null) :
        MusicsResult()

    object ServerError : MusicsResult()
}