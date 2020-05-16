package com.spotifyclone.tools.musicplayer

import android.content.Context
import android.media.MediaPlayer
import com.spotifyclone.tools.basepatterns.SingletonHolder

class SpotifyMediaPlayer private constructor(context: Context) : MediaPlayer() {

    private var stoppedPlayer: Boolean = false

    init {
        super.reset()
    }

    fun prepareMusic(path: String) {
        super.reset()
        super.setDataSource(path)
        super.prepare()
    }

    fun updateParams() {
//        if (looping) super.
    }

    fun playMusic() {
        if (super.isPlaying()) {
            pauseMusic()
            return
        }

        if (stoppedPlayer) {
            super.prepare()
        }
        super.start()
    }

    fun pauseMusic() {
        super.pause()
    }

    fun stopMusic() {
        super.stop()
    }

    companion object : SingletonHolder<SpotifyMediaPlayer, Context>(::SpotifyMediaPlayer)
}