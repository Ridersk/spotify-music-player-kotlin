package com.spotifyclone.presentation.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer

class MusicPlayerNotificationReceiver : BroadcastReceiver() {

    private lateinit var playlistMusicPlayer: PlaylistMusicPlayer
    private lateinit var mContext: Context

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            buildContext(context)
        }

        intent?.let {
            val action = intent.getIntExtra(EXTRA_ACTION, 0)
            when (action) {
                ACTION_PLAY_MUSIC -> {
                    playlistMusicPlayer.tooglePlayMusic()
                }
                ACTION_PREVIOUS_MUSIC -> {
                    playlistMusicPlayer.previousMusic()
                }
                ACTION_NEXT_MUSIC -> {
                    playlistMusicPlayer.nextMusic()
                }
                ACTION_LIKE_MUSIC -> {
                }
                else -> {
                }
            }
            MusicPlayerNotification.getInstance(context!!).updateNotification()
        }
    }

    private fun buildContext(context: Context) {
        mContext = context
        getPlaylistMusicPlayerInstance(context)
    }

    private fun getPlaylistMusicPlayerInstance(context: Context) {
        playlistMusicPlayer = PlaylistMusicPlayer.getInstance(context)
    }

    companion object {
        const val ACTION_OPEN_MUSIC_PLAYER = 0
        const val ACTION_PLAY_MUSIC = 1
        const val ACTION_PREVIOUS_MUSIC = 2
        const val ACTION_NEXT_MUSIC = 3
        const val ACTION_LIKE_MUSIC = 4
        const val EXTRA_ACTION = "EXTRA_ACTION"
    }
}
