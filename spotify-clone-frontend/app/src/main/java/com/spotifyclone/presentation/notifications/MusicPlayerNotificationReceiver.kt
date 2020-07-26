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
            when (intent.action) {
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
        const val ACTION_OPEN_MUSIC_PLAYER = "ACTION_OPEN_MUSIC_PLAYER"
        const val ACTION_PLAY_MUSIC = "ACTION_PLAY_MUSIC"
        const val ACTION_PREVIOUS_MUSIC = "ACTION_PREVIOUS_MUSIC"
        const val ACTION_NEXT_MUSIC = "ACTION_NEXT_MUSIC"
        const val ACTION_LIKE_MUSIC = "ACTION_LIKE_MUSIC"
    }
}
