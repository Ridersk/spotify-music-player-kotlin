package com.spotifyclone.tools.musicplayer

import android.content.Context
import com.spotifyclone.data.model.Music
import com.spotifyclone.tools.basepatterns.SingletonHolder

class PlaylistMusicPlayer private constructor(
    contextActivity: Context
) : MusicPlayer(contextActivity),
    PlaylistObserver<Music>,
    MusicProvider {

    private var originalMusicList = listOf<Music>()
    private var musicQueueBase = mutableListOf<Music>()
    private var musicQueueRunning = mutableListOf<Music>()
    private var positionPlaying = 0
    private var currentMusicId: Long = -1
    private var observers = mutableListOf<MusicObserver>()
    private val cycleModeList = listOf(CYCLE_MODE_OFF, CYCLE_MODE_ONE, CYCLE_MODE_ALL)
    private var currentCycleMode = 0
    private var random = false

    val isCycle: () -> Boolean = { currentCycleMode != 0 }
    val isLastMusic: () -> Boolean =
        { this.positionPlaying == this.musicQueueRunning.size - 1 }
    val getCycleType: () -> Int = { currentCycleMode }
    val isRandom: () -> Boolean = { random }
    val getRandomType: () -> Int = { if (random) 1 else 0 }


    override fun receiverList(list: List<Music>) {
        this.originalMusicList = list.toMutableList()
    }

    override fun chooseItem(id: Long) {
        this.currentMusicId = id
        buildMusicQueue()

        val position = getPositionMusicById(currentMusicId, this.musicQueueRunning)
        if (position != -1) {
            this.positionPlaying = position
        }
        initMusic()
    }

    override fun addObserver(observer: MusicObserver) {
        this.observers.add(observer)
    }

    override fun alertChoosedMusic(music: Music) {
        for (observer in this.observers) {
            observer.chooseMusic(music)
        }
    }

    private fun initMusic(initPlaying: Boolean = true) {
        val music = getCurrentMusic()
        super.playMusic(music.contentUriId, initPlaying)
        alertChoosedMusic(music)
    }

    private fun buildMusicQueue() {
        this.musicQueueBase = if (isRandom()) {
            shuffleQueue(this.originalMusicList)
        } else {
            this.originalMusicList.toMutableList()
        }

        this.musicQueueRunning = initQueue(this.musicQueueBase)

        if (isCycle()) {
            this.musicQueueRunning.addAll(cloneList(this.musicQueueBase))
        }
    }

    private fun initQueue(list: MutableList<Music>): MutableList<Music> {
        val result = mutableListOf<Music>()
        result.addAll(list)
        return result
    }

    private fun cloneList(list: List<Music>): MutableList<Music> {
        val newList = mutableListOf<Music>()

        for (music in list) {
            newList.add(
                Music(
                    title = music.title,
                    artist = music.artist,
                    album = music.album,
                    contentUriId = music.contentUriId,
                    albumUriId = music.albumUriId
                )
            )
        }

        return newList
    }

    fun nextMusic() {
        var position: Int = positionPlaying + 1
        var initPlaying = true

        if (this.musicQueueRunning.size - position <= this.musicQueueBase.size && isCycle()) {
            this.musicQueueRunning =
                concatenateList(this.musicQueueRunning, cloneList(this.musicQueueBase))
        } else if (position >= this.musicQueueRunning.size && !isCycle()) {
            position = 0
            initPlaying = false
        }

        nextMusicQueue(position, initPlaying)
    }

    fun previousMusic() {
        var position: Int

        if (super.getCurrentSec() < 6) {
            position = positionPlaying - 1
            if (position < 0) {
                if (isCycle()) {
                    this.musicQueueRunning =
                        concatenateList(cloneList(this.musicQueueBase), this.musicQueueRunning)
                    position = this.musicQueueBase.size - 1
                } else {
                    position = 0
                }
            }
        } else {
            position = positionPlaying
        }

        nextMusicQueue(position)
    }

    private fun nextMusicQueue(position: Int, initPlaying: Boolean = true) {
        this.currentMusicId = this.musicQueueRunning[position].id

        this.positionPlaying = if (position != -1) position else this.positionPlaying
        initMusic(initPlaying)
    }

    fun getCurrentMusic(): Music {
        return if (positionPlaying >= 0 && positionPlaying < musicQueueRunning.size) {
            musicQueueRunning[positionPlaying]
        } else {
            Music()
        }
    }

    private fun concatenateList(list1: MutableList<Music>, list2: List<Music>): MutableList<Music> {
        val result = mutableListOf<Music>()
        result.addAll(list1)
        result.addAll(list2)
        return result.toMutableList()
    }

    override fun setObserverOnCompletionListener(callbackObserver: () -> Unit) {
        val conditionalCallback = {
            if (isLastMusic() || super.isInit() || super.isEnd()) {
                callbackObserver.invoke()
            } else {
                this.nextMusic()
            }
        }

        super.setObserverOnCompletionListener(conditionalCallback)
    }

    private fun getPositionMusicById(id: Long, musicList: List<Music>): Int {
        if (id != -1L) {
            return musicList.map { music -> music.id }.indexOf(id)
        }
        return 0
    }

    fun toogleRandom() {
        this.random = !this.random

        buildMusicQueue()
        val position = getPositionMusicById(currentMusicId, this.musicQueueRunning)
        if (position != -1) {
            this.positionPlaying = position
        }
    }

    fun toogleModeCycle() {
        this.currentCycleMode = (this.currentCycleMode + 1) % cycleModeList.size

        buildMusicQueue()
        val position = getPositionMusicById(currentMusicId, this.musicQueueRunning)
        if (position != -1) {
            this.positionPlaying = position
        }
    }

    private fun shuffleQueue(list: List<Music>): MutableList<Music> {
        return list.shuffled().toMutableList()
    }

    companion object : SingletonHolder<PlaylistMusicPlayer, Context>(::PlaylistMusicPlayer) {
        const val CYCLE_MODE_ALL = "CYCLE_MODE_ALL"
        const val CYCLE_MODE_ONE = "CYCLE_MODE_ONE"
        const val CYCLE_MODE_OFF = "CYCLE_MODE_OFF"
    }
}