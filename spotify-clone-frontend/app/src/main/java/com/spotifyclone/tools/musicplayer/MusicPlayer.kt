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
    private var observersProgress: MutableList<Pair<Context, ((Int) -> Unit)>> = mutableListOf()
    private var observerNextMusic: (state: Int) -> Unit = {}
    private var progress: Int = 0
    private val observersMusicState: MutableList<Pair<UUID, () -> Unit>> =
        mutableListOf()

    init {
        super.reset()
        musicRefresh()
        spotifyAudioManager = SpotifyAudioManager.getInstance(context)
        registerForObserverOnCompletionListener()
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
            synchronized(this) {
                this.observersMusicState.forEach { observer ->
                    observer.second.invoke()
                }
            }
        } else {
            this.observerNextMusic.invoke(state)
        }
    }

    open fun setObserverOnMusicState(callback: () -> Unit): UUID {
        val id = UUID.randomUUID()
        val observer = Pair(id, callback)

        this.observersMusicState.add(observer)
        return id
    }

    fun removeObserverOnMusicState(id: UUID) {
        val position = this.observersMusicState.indexOfFirst { observer -> observer.first == id }
        if (position >= 0 && position < this.observersMusicState.size) {
            this.observersMusicState.removeAt(position)
        }
    }

    fun setObserverMusicTime(callback: (time: String) -> Unit) {
        observerTimer = callback
    }

    fun removeObserverMusicTime() {
        observerTimer = null
    }

    fun setObserverProgressBar(context: Context, callback: (progress: Int) -> Unit) {
        val observer = Pair(context, callback)
        this.observersProgress.add(observer)
    }

    fun removeObserverProgressBar(context: Context) {
        val position = this.observersProgress.indexOfFirst { observer -> observer.first == context }
        if (position >= 0 && position < this.observersProgress.size) {
            this.observersProgress.removeAt(position)
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
        if (!blockUpdateProgress && observerTimer != null) {
            val time: Pair<Int, Int> = convertToMinutes(currentPosition)
            setTimeOnObserverTimer(time)
        }
    }

    private fun updateProgressbarObserver() {
        if (!blockUpdateProgress && observersProgress.isNotEmpty()) {
            notifyMusicProgress(calculateProgress(currentPosition))
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


    private fun notifyMusicProgress(progress: Int) {
        this.progress = progress

        synchronized(this) {
            this.observersProgress.forEach { observer ->
                observer.second.invoke(progress)
            }
        }
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

    protected fun getCurrentSec(): Int {
        return this.currentPosition / 1000
    }

    val progressControl = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                notifyMusicProgress(progress)
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

    protected fun setObserverOnNextMusic(callback: (state: Int) -> Unit) {
        this.observerNextMusic = callback
    }

    companion object {
        const val STATE_CHANGED_FROM_USER_INPUT = 0
        const val STATE_MUSIC_END_FROM_PLAYER = 1
        const val STATE_RESTART_PLAYLIST = 2
        const val STATE_NEXT_MUSIC_FROM_USER = 3
    }
}
