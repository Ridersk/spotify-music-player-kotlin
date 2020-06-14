package com.spotifyclone.tools.musicplayer

import android.content.Context
import android.media.MediaPlayer
import android.widget.SeekBar
import com.spotifyclone.tools.filemanager.MusicFileManagerApp
import java.io.FileDescriptor
import java.util.*

open class MusicPlayer(var context: Context) : MediaPlayer() {

    private val spotifyAudioManager: SpotifyAudioManager
    private var stoppedPlayer: Boolean = false
    private var currentMusic: FileDescriptor? = null
    private var musicDurationMilisec: Int = 1
    private var blockUpdateProgress = false
    private var observerTimer: ((String) -> Unit)? = null
    private var observerProgress: ((Int) -> Unit)? = null
    private var progress: Int = 0
    private var observerStatusPlaying: () -> Unit = {}

    init {
        super.reset()
        musicRefresh()
        spotifyAudioManager = SpotifyAudioManager.getInstance(context)
    }

    fun tooglePlayMusic() {
        if (super.isPlaying()) {
            pauseMusic()
            return
        }

        this.startMusic()
    }

    protected fun playMusic(contentUriId: Long, initPlaying: Boolean = true) {
        super.reset()
        setMusicAttrs(contentUriId)
        super.setDataSource(currentMusic)
        super.prepare()

        if (initPlaying) {
            this.startMusic()
        }
    }

    private fun startMusic() {
        if (stoppedPlayer) {
            super.prepare()
        }

        spotifyAudioManager.startMusic({ super.start() }, { this.pauseMusic() })
    }

    protected fun restartMusic() {
        super.seekTo(0)
        this.startMusic()
    }

    private fun pauseMusic() {
        super.pause()
    }

    open fun setObserverOnCompletionListener(callbackObserver: () -> Unit) {
        super.setOnCompletionListener {
            if (!super.isPlaying()) {
                callbackObserver.invoke()
            }
        }

        this.observerStatusPlaying = callbackObserver
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

    fun getTotalTime(): String {
        val time: Pair<Int, Int> = convertToMinutes(musicDurationMilisec)
        return convertToFormatTimer(time)
    }

    private fun setTimeOnObserverTimer(time: Pair<Int, Int>) {
        observerTimer!!.invoke(convertToFormatTimer(time))
    }

    private fun convertToFormatTimer(time: Pair<Int, Int>): String =
        "${time.first}:${if (time.second < 10) "0" else ""}${time.second}"


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

    private fun getProgress(): Int = calculateProgress(currentPosition)

    fun isInit():Boolean = getProgress() == 0

    fun isEnd():Boolean = getProgress() == 100

    private fun calculateProgress(positionMilisec: Int): Int =
        (positionMilisec * 100) / musicDurationMilisec

    private fun calculateMiliseconds(progress: Int): Int =
        (progress * musicDurationMilisec) / 100

    protected fun getCurrentSec(): Int {
        return this.currentPosition / 10000
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

}