package com.spotifyclone.presentation.musicqueue

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spotifyclone.R
import com.spotifyclone.presentation.base.BaseFragment
import com.spotifyclone.tools.musicplayer.MusicObserver
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import kotlinx.android.synthetic.main.fragment_music_player_queue.*

class MusicPlayerQueueFragment(parentContext: Context) : BaseFragment(), MusicObserver {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(parentContext)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music_player_queue, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playlistMusicPlayer.addMusicObserver(this)
        super.onViewCreated(view, savedInstanceState)
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
    }

    override fun changedProgress(progress: Int) {
        progressBar?.progress = progress
    }

    override fun changedMusicState() {
        btnPlayPause.isActivated = playlistMusicPlayer.isPlaying
    }

    override fun removeComponents() {
        playlistMusicPlayer.removeMusicObserver(this)
    }

    companion object {
        fun getInstance(
            context: Context
        ): MusicPlayerQueueFragment {
            return MusicPlayerQueueFragment(context)
        }
    }
}
