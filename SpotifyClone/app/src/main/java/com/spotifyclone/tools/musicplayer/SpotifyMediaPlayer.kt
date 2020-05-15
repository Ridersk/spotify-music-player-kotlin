package com.spotifyclone.tools.musicplayer

import android.content.Context
import android.media.MediaPlayer
import com.spotifyclone.tools.basepatterns.SingletonHolder

class SpotifyMediaPlayer private constructor(context: Context) : MediaPlayer() {
    init {
        // TODO
    }

    fun playMusic(path: String) {
        super.stop()
        super.setDataSource(path)
        super.prepare()
        super.start()
    }

    companion object : SingletonHolder<SpotifyMediaPlayer, Context>(::SpotifyMediaPlayer)
}