package com.spotifyclone.presentation.musicqueue

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spotifyclone.R
import com.spotifyclone.presentation.base.BaseFragment
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import kotlinx.android.synthetic.main.fragment_music_player_queue.*
import java.util.*

class MusicPlayerQueueFragment(parentContext: Context) : BaseFragment() {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(parentContext)
    private lateinit var idCallbackStateMusic: UUID

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music_player_queue, container, false)
    }

    override fun initComponents() {
        btncPrevious.setOnClickListener {
            playlistMusicPlayer.previousMusic()
        }
        btnNext.setOnClickListener {
            playlistMusicPlayer.nextMusic()
        }
        btnPlayPause.isActivated = playlistMusicPlayer.isPlaying
        btnPlayPause.setOnClickListener {
            playlistMusicPlayer.tooglePlayMusic()
            btnPlayPause.isActivated = playlistMusicPlayer.isPlaying
        }
        idCallbackStateMusic = playlistMusicPlayer.setObserverOnMusicState {
            btnPlayPause.isActivated = playlistMusicPlayer.isPlaying
        }
    }

    override fun removeComponents() {
        playlistMusicPlayer.removeObserverOnMusicState(idCallbackStateMusic)
    }

    companion object {
        fun getInstance(
            context: Context
        ): MusicPlayerQueueFragment {
            return MusicPlayerQueueFragment(context)
        }
    }
}