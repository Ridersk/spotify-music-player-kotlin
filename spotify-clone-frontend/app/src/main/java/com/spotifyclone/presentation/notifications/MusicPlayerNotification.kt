package com.spotifyclone.presentation.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.presentation.notifications.MusicPlayerNotificationReceiver.Companion.ACTION_LIKE_MUSIC
import com.spotifyclone.presentation.notifications.MusicPlayerNotificationReceiver.Companion.ACTION_NEXT_MUSIC
import com.spotifyclone.presentation.notifications.MusicPlayerNotificationReceiver.Companion.ACTION_PLAY_MUSIC
import com.spotifyclone.presentation.notifications.MusicPlayerNotificationReceiver.Companion.ACTION_PREVIOUS_MUSIC
import com.spotifyclone.presentation.notifications.MusicPlayerNotificationReceiver.Companion.EXTRA_ACTION
import com.spotifyclone.tools.basepatterns.SingletonHolder
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import com.spotifyclone.tools.utils.ImageUtils
import java.util.*

class MusicPlayerNotification private constructor(private val context: Context) {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(context)
    private lateinit var idCallbackStateMusic: UUID
    private val notificationManager = NotificationManagerCompat.from(context.applicationContext)
    private val notificationLayout =
        RemoteViews(context.packageName, R.layout.fragment_music_player_notification)
    private val notification = NotificationCompat.Builder(context, CHANNEL_1_ID)
        .setSmallIcon(R.mipmap.ic_launcher_spotify)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCustomBigContentView(notificationLayout)

    init {
        createNotificationChannel()
        createCallbacks()
    }

    fun createNotification() {
        val playMusicIntent = Intent(context, MusicPlayerNotificationReceiver::class.java)
        val previousMusicIntent = Intent(context, MusicPlayerNotificationReceiver::class.java)
        val nextMusicIntent = Intent(context, MusicPlayerNotificationReceiver::class.java)
        val likeMusicIntent = Intent(context, MusicPlayerNotificationReceiver::class.java)

        playMusicIntent.putExtra(EXTRA_ACTION, ACTION_PLAY_MUSIC)
        previousMusicIntent.putExtra(EXTRA_ACTION, ACTION_PREVIOUS_MUSIC)
        nextMusicIntent.putExtra(EXTRA_ACTION, ACTION_NEXT_MUSIC)
        likeMusicIntent.putExtra(EXTRA_ACTION, ACTION_LIKE_MUSIC)

        notificationLayout.setOnClickPendingIntent(
            R.id.btnPlayMusic,
            PendingIntent.getBroadcast(context, 0, playMusicIntent, 0)
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.btnPreviousMusic,
            PendingIntent.getBroadcast(context, 0, previousMusicIntent, 0)
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.btnNextMusic,
            PendingIntent.getBroadcast(context, 0, nextMusicIntent, 0)
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.btnLike,
            PendingIntent.getBroadcast(context, 0, likeMusicIntent, 0)
        )

        notificationManager.notify(NOTIFICATION_MUSIC_PLAYER_ID, notification.build())

        updateNotification()
    }

    fun updateNotification() {
        val playMusicDrawable =
            if (playlistMusicPlayer.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        val music = playlistMusicPlayer.getCurrentMusic()?: Music()
        val image = ImageUtils.getBitmapAlbumArt(context, music.albumUriId?:-1L)

        notificationLayout.setImageViewResource(
            R.id.btnPlayMusic,
            playMusicDrawable
        )
        notificationLayout.setImageViewBitmap(R.id.imgAlbum, image)
        notificationLayout.setTextViewText(R.id.txtMusicTitle, music.title)
        notificationLayout.setTextViewText(R.id.txtMusicArtist, music.artist)

        notificationManager.notify(
            NOTIFICATION_MUSIC_PLAYER_ID,
            notification.build()
        )
    }

    private fun createCallbacks() {
        idCallbackStateMusic = playlistMusicPlayer.setObserverOnMusicState {
            updateNotification()
        }
    }

    private fun destroyNotification() {
        playlistMusicPlayer.removeObserverOnMusicState(idCallbackStateMusic)
        notificationManager.cancel(NOTIFICATION_MUSIC_PLAYER_ID)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                CHANNEL_1_ID,
                "MusicPlayerNotificationChannel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager: NotificationManager? =
                ContextCompat.getSystemService(
                    context.applicationContext,
                    NotificationManager::class.java
                )

            channel1.enableVibration(false)
            channel1.enableLights(false)
            channel1.vibrationPattern = longArrayOf(0L)
            manager?.let {
                manager.createNotificationChannel(channel1)
            }
        }
    }

    companion object :
        SingletonHolder<MusicPlayerNotification, Context>(::MusicPlayerNotification) {
        const val NOTIFICATION_MUSIC_PLAYER_ID = 1
        const val CHANNEL_1_ID = "musicPlayerNotificationChannel1"
    }
}
