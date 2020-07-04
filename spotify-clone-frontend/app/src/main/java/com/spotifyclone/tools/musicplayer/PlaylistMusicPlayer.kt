package com.spotifyclone.tools.musicplayer

import android.content.Context
import com.spotifyclone.data.model.Music
import com.spotifyclone.tools.basepatterns.SingletonHolder
import com.spotifyclone.tools.utils.ListUtils
import java.util.*

class PlaylistMusicPlayer private constructor(
    contextActivity: Context
) : MusicPlayer(contextActivity),
    PlaylistObserver<Music>,
    MusicProvider {

    private var originalMusicList = listOf<Music>()
    private var normalMusicQueueBase: MutableList<Music> = mutableListOf()
    var normalMusicQueueRunning: MutableList<Music> = mutableListOf()
        private set
    var priorityMusicQueue: MutableList<Music> = mutableListOf()
        private set
    var positionPlaying: Int = 0
        private set
    private lateinit var currentMusicId: UUID
    private var observers: MutableList<MusicObserver> = mutableListOf()
    private val modeCycleList: List<String> = listOf(CYCLE_MODE_OFF, CYCLE_MODE_ALL, CYCLE_MODE_ONE)
    private var currentModeCycle = 0
    private var random = false

    val isCycle: () -> Boolean = { currentModeCycle != 0 }
    val getCycleType: () -> Int = { currentModeCycle }
    val isRandom: () -> Boolean = { random }
    val getRandomType: () -> Int = { if (random) 1 else 0 }

    init {
        super.setObserverOnNextMusic(this::handleCallbackNextMusic)
    }

    private fun handleCallbackNextMusic(state: Int) {
        if (state == STATE_MUSIC_END_FROM_PLAYER &&
            modeCycleList[currentModeCycle] == CYCLE_MODE_ONE
        ) {
            super.restartMusic()
        } else if (state == STATE_MUSIC_END_FROM_PLAYER) {
            this.nextMusic(STATE_MUSIC_END_FROM_PLAYER)
        }
    }

    override fun receiverList(list: List<Music>) {
        this.originalMusicList = list.toMutableList()
    }

    override fun chooseMusic(id: UUID) {
        this.currentMusicId = id
        buildMusicQueue()

        val position = getPositionMusicById(currentMusicId, this.normalMusicQueueRunning)
        if (position != -1) {
            this.positionPlaying = position
        }
        val music = getCurrentMusic()
        initMusic(music, STATE_NEXT_MUSIC_FROM_USER)
    }

    override fun addMusicObserver(observer: MusicObserver) {
        this.observers.add(observer)
    }

    override fun removeMusicObserver(observer: MusicObserver) {
        this.observers.remove(observer)
    }

    override fun notifyChangedMusic(music: Music) {
        synchronized(this) {
            this.observers.forEach { observer ->
                observer.changedMusic(music)
            }
        }
    }

    override fun notifyUpdateList(musicList: List<Music>) {
        synchronized(this) {
            this.observers.forEach { observer ->
                observer.updatedList(musicList)
            }
        }
    }

    fun getCompleteListInContext(): List<Music> {
        val completeList: MutableList<Music> = mutableListOf()
        if (::currentMusicId.isInitialized) {
            val position = getPositionMusicById(currentMusicId, this.normalMusicQueueRunning)
            completeList.addAll(
                ListUtils.cut(
                    normalMusicQueueRunning,
                    position + 1
                ).toMutableList()
            )
            completeList.addAll(priorityMusicQueue)
            completeList.addAll(ListUtils.sublist(normalMusicQueueRunning, position + 1))
        }
        return completeList
    }

    private fun initMusic(music: Music, state: Int) {
        super.playMusicFromPlaylist(music.contentUriId, state)
        notifyChangedMusic(music)
    }

    private fun buildMusicQueue() {
        if (isRandom()) {
            val shuffledList: MutableList<Music> = shuffleQueue(this.originalMusicList)
            val position = getPositionMusicById(currentMusicId, shuffledList)
            Collections.swap(shuffledList, 0, position)
            this.positionPlaying = 0
            this.normalMusicQueueBase = shuffledList
        } else {
            this.normalMusicQueueBase = this.originalMusicList.toMutableList()
            this.positionPlaying =
                getPositionMusicById(currentMusicId, this.normalMusicQueueRunning)
        }

        this.normalMusicQueueRunning = initQueue(this.normalMusicQueueBase)

        if (isCycle()) {
            this.normalMusicQueueRunning.addAll(copyList(this.normalMusicQueueBase))
        }

        this.priorityMusicQueue = mutableListOf()
    }

    private fun initQueue(list: MutableList<Music>): MutableList<Music> {
        val result = mutableListOf<Music>()
        result.addAll(list)
        return result
    }

    private fun copyList(list: List<Music>): MutableList<Music> {
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

    fun nextMusic(state: Int = STATE_NEXT_MUSIC_FROM_USER) {
        var nState = state
        if (priorityMusicQueue.isEmpty()) {
            var position: Int = positionPlaying + 1

            if (this.normalMusicQueueRunning.size - position <= this.normalMusicQueueBase.size && isCycle()) {
                this.normalMusicQueueRunning =
                    concatenateList(
                        this.normalMusicQueueRunning,
                        copyList(this.normalMusicQueueBase)
                    )
            } else if (position >= this.normalMusicQueueRunning.size && !isCycle()) {
                this.normalMusicQueueRunning = this.normalMusicQueueBase
                position = 0
                nState = STATE_RESTART_PLAYLIST
            }

            nextMusicQueue(position, nState)
        } else {
            nextMusicFromPriorityQueue()
        }
    }

    fun previousMusic() {
        var position: Int

        if (super.getCurrentSec() < 6) {
            position = positionPlaying - 1
            if (position < 0) {
                if (isCycle()) {
                    this.normalMusicQueueRunning =
                        concatenateList(
                            copyList(this.normalMusicQueueBase),
                            this.normalMusicQueueRunning
                        )
                    position = this.normalMusicQueueBase.size - 1
                } else {
                    position = 0
                }
            }
        } else {
            position = positionPlaying
        }

        nextMusicQueue(position, STATE_NEXT_MUSIC_FROM_USER)
    }

    private fun nextMusicQueue(position: Int, state: Int) {
        if (position >= 0 && position < this.normalMusicQueueRunning.size) {
            this.currentMusicId = this.normalMusicQueueRunning[position].id
        } else {
            this.currentMusicId = this.normalMusicQueueRunning[0].id
        }

        this.positionPlaying = if (position != -1) position else this.positionPlaying
        val music = getCurrentMusic()
        initMusic(music, state)
    }

    private fun nextMusicFromPriorityQueue() {
        this.currentMusicId = this.priorityMusicQueue[0].id
        val music = this.priorityMusicQueue[0]
        initMusic(music, STATE_NEXT_MUSIC_FROM_USER)
        this.priorityMusicQueue.removeAt(0)
    }

    fun getCurrentMusic(): Music {
        return if (positionPlaying >= 0 && positionPlaying < normalMusicQueueRunning.size) {
            normalMusicQueueRunning[positionPlaying]
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

    private fun getPositionMusicById(id: UUID, musicList: List<Music>): Int {
        return musicList.map { music -> music.id }.indexOf(id)
    }

    fun toogleRandom() {
        this.random = !this.random
        buildMusicQueue()
    }

    fun toogleModeCycle() {
        this.currentModeCycle = (this.currentModeCycle + 1) % modeCycleList.size

        buildMusicQueue()
        val position = getPositionMusicById(currentMusicId, this.normalMusicQueueRunning)
        if (position != -1) {
            this.positionPlaying = position
        }
    }

    private fun shuffleQueue(list: List<Music>): MutableList<Music> {
        return list.shuffled().toMutableList()
    }

    fun recreatePriorityMusicQueue(musics: List<Music>) {
        this.priorityMusicQueue = copyList(musics)
        this.notifyUpdateList(getCompleteListInContext())
    }

    fun updateMusicsFromNormalList(musics: List<Music>, start: Int) {
        this.normalMusicQueueRunning =
            ListUtils.swapAllAt(this.normalMusicQueueRunning, musics, start) as MutableList<Music>
        this.notifyUpdateList(getCompleteListInContext())
    }

    fun firstNotPlayedFromNormalQueue(): Int {
        if (this.positionPlaying < this.normalMusicQueueRunning.size - 1) {
            return this.positionPlaying + 1
        }
        return this.normalMusicQueueRunning.size
    }

    companion object : SingletonHolder<PlaylistMusicPlayer, Context>(::PlaylistMusicPlayer) {
        const val CYCLE_MODE_ALL = "CYCLE_MODE_ALL"
        const val CYCLE_MODE_ONE = "CYCLE_MODE_ONE"
        const val CYCLE_MODE_OFF = "CYCLE_MODE_OFF"
    }
}
