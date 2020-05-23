package com.spotifyclone.tools.musicplayer

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import android.widget.SeekBar
import com.spotifyclone.tools.basepatterns.SingletonHolder
import java.io.FileDescriptor
import java.util.*

class SpotifyMediaController private constructor(val context: Context) : MediaPlayer() {

    private var stoppedPlayer: Boolean = false
    private var currentMusic: FileDescriptor? = null
    private val mediaMetada = MediaMetadataRetriever()
    private var musicDurationMilisec: Int = 1
    private var blockUpdateProgress = false
    private var observerTimer: ((String) -> Unit)? = null
    private var observerProgress: ((Int) -> Unit)? = null
    private var progress: Int = 0

    init {
        super.reset()
        musicRefresh()
    }

    fun prepareMusic(contentUriId: Long) {
        super.reset()
        currentMusic = getAudioFile(getUri(contentUriId))
        mediaMetada.setDataSource(currentMusic)
        musicDurationMilisec =
            mediaMetada.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toInt()
        super.setDataSource(currentMusic)
        super.prepare()
    }

    private fun getAudioFile(uri: Uri) = context.contentResolver
        .openFileDescriptor(uri, "r")?.fileDescriptor

    private fun getUri (contentUriId: Long): Uri =
        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentUriId)

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

    private fun pauseMusic() {
        super.pause()
    }

    fun setObserverOnCompletion(callback: () -> Unit) {
        super.setOnCompletionListener {
            if (!super.isPlaying()) {
                callback.invoke()
            }
        }
    }

    fun setObserverMusicTime(callback: (time: String) -> Unit) {
        observerTimer = callback
    }

    fun setObserverProgressBar(callback: (progress: Int) -> Unit) {
        observerProgress = callback
    }

    private fun musicRefresh() {
        Timer().scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    updateProgressbarObserver()
                }
            },
            0,
            50
        )

        Timer().scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    updateTimerObserver()
                }
            },
            0,
            1000
        )
    }

    private fun updateTimerObserver() {
        if (!blockUpdateProgress && observerTimer != null) {
            val time: Pair<Int, Int> = convertToMinutes(currentPosition)
            setTimeOnObserverTimer(time)
        }
    }

    private fun updateProgressbarObserver() {
        if (!blockUpdateProgress && observerProgress != null) {
            setProgressOnObserverProgress(calculateProgress(currentPosition))
        }
    }

    private fun setTimeOnObserverTimer(time: Pair<Int, Int>) {
        observerTimer!!.invoke(
            "${time.first}:${if (time.second < 10) "0" else ""}${time.second}"
        )
    }

    private fun setProgressOnObserverProgress(progress: Int) {
        this.progress = progress
        observerProgress!!.invoke(progress)
    }

    private fun updateProgressOnMusicPlayer() {
        super.seekTo(calculateMiliseconds(progress))
    }

    val progressControl = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                setProgressOnObserverProgress(progress)
                setTimeOnObserverTimer(convertToMinutes(calculateMiliseconds(progress)))
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            blockUpdateProgress = true

        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            blockUpdateProgress = false
            updateProgressOnMusicPlayer()
        }

    }

    private fun convertToMinutes(milisec: Int): Pair<Int, Int> {
        val minutes: Int = milisec / 1000 / 60
        val seconds: Int = (milisec / 1000) % 60
        return Pair(minutes, seconds)
    }

    private fun calculateProgress(positionMilisec: Int): Int =
        (positionMilisec * 100) / musicDurationMilisec

    private fun calculateMiliseconds(progress: Int): Int =
        (progress * musicDurationMilisec) / 100

    companion object : SingletonHolder<SpotifyMediaController, Context>(::SpotifyMediaController)
}