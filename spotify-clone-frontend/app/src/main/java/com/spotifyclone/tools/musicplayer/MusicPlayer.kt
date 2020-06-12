package com.spotifyclone.tools.musicplayer

import android.content.Context
import android.media.MediaPlayer
import android.widget.SeekBar
import com.spotifyclone.tools.filemanager.MusicFileManagerApp
import java.io.FileDescriptor
import java.util.*

open class MusicPlayer (var context: Context) : MediaPlayer() {

    private val spotifyAudioManager: SpotifyAudioManager
    private var stoppedPlayer: Boolean = false
    private var currentMusic: FileDescriptor? = null
    private var musicDurationMilisec: Int = 1
    private var blockUpdateProgress = false
    private var observerTimer: ((String) -> Unit)? = null
    private var observerProgress: ((Int) -> Unit)? = null
    private var progress: Int = 0

    private var observerStatusPlaying: () -> Unit = {}

    private var stopedControls = false

    init {
        super.reset()
        musicRefresh()
        spotifyAudioManager = SpotifyAudioManager.getInstance(context)
    }

    fun playMusic(contentUriId: Long) {
        super.reset()
        setMusicAttrs(contentUriId)
        super.setDataSource(currentMusic)
        super.prepare()
        this.startMusic()
    }

    fun playMusic() {
        if (!stopedControls) {
            this.startMusic()
        }
    }

    private fun startMusic() {
        if (super.isPlaying()) {
            pauseMusic()
            return
        }

        if (stoppedPlayer) {
            super.prepare()
        }

        spotifyAudioManager.startMusic({ super.start() }, { this.pauseMusic() })
    }

    private fun pauseMusic() {
        super.pause()
        this.observerStatusPlaying.invoke()
    }

    fun setObserverOnCompletionListener(callback: () -> Unit) {
        super.setOnCompletionListener {
            if (!super.isPlaying()) {
                callback.invoke()
            }
        }

        this.observerStatusPlaying = callback
    }

    fun setObserverMusicTime(callback: (time: String) -> Unit) {
        observerTimer = callback
    }

    fun setObserverProgressBar(callback: (progress: Int) -> Unit) {
        observerProgress = callback
    }

    private fun setMusicAttrs(contentUriId: Long) {
        currentMusic = MusicFileManagerApp.getAudioFile(contentUriId, context)
        musicDurationMilisec = MusicFileManagerApp.getMusicDuration(currentMusic)
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

    private fun convertToMinutes(milisec: Int): Pair<Int, Int> {
        val minutes: Int = milisec / 1000 / 60
        val seconds: Int = (milisec / 1000) % 60
        return Pair(minutes, seconds)
    }

    private fun calculateProgress(positionMilisec: Int): Int =
        (positionMilisec * 100) / musicDurationMilisec

    private fun calculateMiliseconds(progress: Int): Int =
        (progress * musicDurationMilisec) / 100

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

}