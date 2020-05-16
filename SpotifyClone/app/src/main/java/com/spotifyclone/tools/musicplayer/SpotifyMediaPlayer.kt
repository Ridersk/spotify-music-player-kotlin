package com.spotifyclone.tools.musicplayer

import android.content.ContentUris
import android.content.Context
import android.media.MediaPlayer
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import com.spotifyclone.tools.basepatterns.SingletonHolder
import java.io.FileDescriptor

class SpotifyMediaPlayer private constructor(val context: Context) : MediaPlayer() {

    private var stoppedPlayer: Boolean = false
    private var currentMusic: FileDescriptor? = null

    init {
        super.reset()
    }

    fun prepareMusic(contentUriId: Long) {
        super.reset()
        
        currentMusic = getAudioFile(contentUriId)
        super.setDataSource(currentMusic)
        super.prepare()
    }

    private fun getAudioFile(contentUriId: Long) = context.contentResolver.openFileDescriptor(
        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentUriId),
        "r"
    )?.fileDescriptor

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

    fun setObserversOnCompletion(observers: List<() -> Unit>) {
        for (callback in observers) {
            super.setOnCompletionListener {callback.invoke()}
        }
    }

    companion object : SingletonHolder<SpotifyMediaPlayer, Context>(::SpotifyMediaPlayer)
}