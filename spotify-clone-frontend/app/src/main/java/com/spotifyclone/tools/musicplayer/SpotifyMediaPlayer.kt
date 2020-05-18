package com.spotifyclone.tools.musicplayer

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.MediaStore
import androidx.core.os.postAtTime
import com.spotifyclone.tools.basepatterns.SingletonHolder
import java.io.FileDescriptor
import java.util.*

class SpotifyMediaPlayer private constructor(val context: Context) : MediaPlayer() {

    private var stoppedPlayer: Boolean = false
    private var currentMusic: FileDescriptor? = null
    private val mediaMetada = MediaMetadataRetriever()
    private var musicDuration: Int = 1

    init {
        super.reset()
    }

    fun prepareMusic(contentUriId: Long) {
        super.reset()
        currentMusic = getAudioFile(contentUriId)
        mediaMetada.setDataSource(currentMusic)
        musicDuration = mediaMetada.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toInt()
        super.setDataSource(currentMusic)
        super.prepare()
    }

    private fun getAudioFile(contentUriId: Long) = context.contentResolver.openFileDescriptor(
        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentUriId),
        "r"
    )?.fileDescriptor

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

    fun setObserversOnCompletion(callback: () -> Unit) {
        super.setOnCompletionListener {
            if (!super.isPlaying()) {
                callback.invoke()
            }
        }
    }

    fun setObserversProgressBar(callback: (progress: Int) -> Unit) {
        updateProgressBar(callback)
    }

    private fun updateProgressBar(callback: (progress: Int) -> Unit) {

        Timer().scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    val progress: Int = (currentPosition * 100)/musicDuration
                    callback.invoke(progress)
                }
            },
            0,
            200
        )
    }

    companion object : SingletonHolder<SpotifyMediaPlayer, Context>(::SpotifyMediaPlayer)
}