package com.spotifyclone.tools.musicplayer

import android.content.Context
import android.media.MediaPlayer
import android.widget.SeekBar
import com.spotifyclone.tools.filemanager.MusicFileManagerApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.FileDescriptor
import java.util.*

open class MusicPlayer(var context: Context) : MediaPlayer(), MusicProvider {

    private val spotifyAudioManager: SpotifyAudioManager
    private var stoppedPlayer: Boolean = false
    private var currentMusic: FileDescriptor? = null
    private var musicDurationMilisec: Int = 1
    private var blockUpdateProgress = false
    protected val observers: MutableList<MusicObserver> = mutableListOf()
    private var callbackHandlerNextMusic: (state: Int) -> Unit = {}
    private var progress: Int = 0

    init {
        super.reset()
        musicRefresh()
        spotifyAudioManager = SpotifyAudioManager.getInstance(context)
        registerForObserverOnCompletionListener()
    }

    override fun addMusicObserver(observer: MusicObserver) {
        this.observers.add(observer)
    }

    override fun changeMusicState() {
        runBlocking {
            launch(Dispatchers.Default) {
                observers.forEach { observer ->
                    observer.changedMusicState()
                }
            }
        }
    }

    override fun changeProgress(progress: Int) {
        this.progress = progress
        runBlocking {
            launch(Dispatchers.Default) {
                observers.forEach { observer ->
                    observer.changedProgress(progress)
                }
            }
        }
    }

    override fun changeMusicTimer(time: Pair<Int, Int>) {
        runBlocking {
            launch(Dispatchers.Default) {
                val timeStr = convertToFormatTimer(time)
                observers.forEach { observer ->
                    observer.changedMusicTimer(timeStr)
                }
            }
        }
    }

    override fun removeMusicObserver(observer: MusicObserver) {
        this.observers.remove(observer)
    }

    fun tooglePlayMusic() {
        if (super.isPlaying()) {
            pauseMusic()
            this.notifyObserversWhenStateChange(STATE_CHANGED_FROM_USER_INPUT)
            return
        }

        this.startMusic()
        this.notifyObserversWhenStateChange(STATE_CHANGED_FROM_USER_INPUT)
    }

    protected fun playMusicFromPlaylist(contentUriId: Long, state: Int) {
        if (contentUriId != -1L) {
            super.reset()
            setMusicAttrs(contentUriId)
            super.setDataSource(currentMusic)
            super.prepare()

            if (state != STATE_RESTART_PLAYLIST) {
                this.startMusic()
            }
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

    private fun registerForObserverOnCompletionListener() {
        super.setOnCompletionListener {
            if (!super.isPlaying() && getCurrentSec() > 0) {
                notifyObserversWhenStateChange(STATE_MUSIC_END_FROM_PLAYER)
            }
        }
    }

    private fun notifyObserversWhenStateChange(state: Int) {
        if (state == STATE_CHANGED_FROM_USER_INPUT || (state == STATE_RESTART_PLAYLIST)) {
            changeMusicState()
        } else {
            this.callbackHandlerNextMusic.invoke(state)
        }
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
        if (!blockUpdateProgress) {
            val time: Pair<Int, Int> = convertToMinutes(currentPosition)
            changeMusicTimer(time)
        }
    }

    private fun updateProgressbarObserver() {
        if (!blockUpdateProgress && observers.isNotEmpty()) {
            changeProgress(calculateProgress(currentPosition))
        }
    }

    fun getTotalTime(): String {
        val time: Pair<Int, Int> = convertToMinutes(musicDurationMilisec)
        return convertToFormatTimer(time)
    }

    private fun convertToFormatTimer(time: Pair<Int, Int>): String =
        "${time.first}:${if (time.second < 10) "0" else ""}${time.second}"

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

    protected fun getCurrentSec(): Int {
        return this.currentPosition / 1000
    }

    val progressControl = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                changeProgress(progress)
                changeMusicTimer(convertToMinutes(calculateMiliseconds(progress)))
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

    protected fun setObserverOnNextMusic(callback: (state: Int) -> Unit) {
        this.callbackHandlerNextMusic = callback
    }

    companion object {
        const val STATE_CHANGED_FROM_USER_INPUT = 0
        const val STATE_MUSIC_END_FROM_PLAYER = 1
        const val STATE_RESTART_PLAYLIST = 2
        const val STATE_NEXT_MUSIC_FROM_USER = 3
    }
}
