package com.spotifyclone.presentation.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.presentation.music.MusicPlayerActivity
import com.spotifyclone.presentation.notifications.MusicPlayerNotificationReceiver.Companion.ACTION_LIKE_MUSIC
import com.spotifyclone.presentation.notifications.MusicPlayerNotificationReceiver.Companion.ACTION_NEXT_MUSIC
import com.spotifyclone.presentation.notifications.MusicPlayerNotificationReceiver.Companion.ACTION_PLAY_MUSIC
import com.spotifyclone.presentation.notifications.MusicPlayerNotificationReceiver.Companion.ACTION_PREVIOUS_MUSIC
import com.spotifyclone.tools.musicplayer.MusicObserver
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import com.spotifyclone.tools.utils.ImageUtils

class MusicPlayerNotification : Service(), MusicObserver {

    private val binder: MusicPlayerNotificationBinder = MusicPlayerNotificationBinder()
    private lateinit var playlistMusicPlayer: PlaylistMusicPlayer
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notificationLayout: RemoteViews
    private lateinit var notification: NotificationCompat.Builder

    override fun onBind(intent: Intent?): IBinder? {
        playlistMusicPlayer = PlaylistMusicPlayer.getInstance(applicationContext)
        playlistMusicPlayer.addMusicObserver(this)
        notificationManager = NotificationManagerCompat.from(applicationContext.applicationContext)
        notificationLayout =
            RemoteViews(applicationContext.packageName, R.layout.fragment_music_player_notification)
        notification = NotificationCompat.Builder(applicationContext, CHANNEL_1_ID)
            .setSmallIcon(R.mipmap.ic_launcher_spotify)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCustomBigContentView(notificationLayout)
            .setSound(null)
        createNotificationChannel()
        createNotification()
        return binder
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val closeManager = NotificationManagerCompat.from(applicationContext.applicationContext)
        closeManager.cancel(NOTIFICATION_MUSIC_PLAYER_ID)
        super.onTaskRemoved(rootIntent)
    }

    override fun changedMusic(music: Music) {
        updateNotification()
    }

    override fun changedMusicState() {
        updateNotification()
    }

    private fun createNotification() {
        val notificationBuilded = notification.build()
        val playMusicIntent =
            Intent(applicationContext, MusicPlayerNotificationReceiver::class.java)
        val previousMusicIntent =
            Intent(applicationContext, MusicPlayerNotificationReceiver::class.java)
        val nextMusicIntent =
            Intent(applicationContext, MusicPlayerNotificationReceiver::class.java)
        val likeMusicIntent =
            Intent(applicationContext, MusicPlayerNotificationReceiver::class.java)
        val musicPlayerIntent = Intent(applicationContext, MusicPlayerActivity::class.java)

        playMusicIntent.action = ACTION_PLAY_MUSIC
        previousMusicIntent.action = ACTION_PREVIOUS_MUSIC
        nextMusicIntent.action = ACTION_NEXT_MUSIC
        likeMusicIntent.action = ACTION_LIKE_MUSIC

        musicPlayerIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        notificationBuilded.flags = NotificationCompat.FLAG_AUTO_CANCEL

        notificationLayout.setOnClickPendingIntent(
            R.id.fragmentMusicPlayerNotification,
            PendingIntent.getActivity(applicationContext, 0, musicPlayerIntent, 0)
        )

        notificationLayout.setOnClickPendingIntent(
            R.id.btnPlayMusic,
            PendingIntent.getBroadcast(applicationContext, 0, playMusicIntent, 0)
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.btnPreviousMusic,
            PendingIntent.getBroadcast(applicationContext, 0, previousMusicIntent, 0)
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.btnNextMusic,
            PendingIntent.getBroadcast(applicationContext, 0, nextMusicIntent, 0)
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.btnLike,
            PendingIntent.getBroadcast(applicationContext, 0, likeMusicIntent, 0)
        )

        notificationManager.notify(NOTIFICATION_MUSIC_PLAYER_ID, notification.build())

        updateNotification()
    }

    private fun updateNotification() {
        val playMusicDrawable =
            if (playlistMusicPlayer.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        val music = playlistMusicPlayer.getCurrentMusic() ?: Music()
        val image = ImageUtils.getBitmapAlbumArt(applicationContext, music.albumUriId ?: -1L)

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


        if (applicationContext.isDarkThemeOn()) {
            notificationLayout.setInt(R.id.btnPlayMusic, "setColorFilter", R.color.white)
            notificationLayout.setInt(R.id.btnPreviousMusic, "setColorFilter", R.color.white)
            notificationLayout.setInt(R.id.btnNextMusic, "setColorFilter", R.color.white)
            notificationLayout.setInt(R.id.btnLike, "setColorFilter", R.color.white)
        }

    }

    private fun Context.isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                CHANNEL_1_ID,
                "MusicPlayerNotificationChannel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel1.enableVibration(false)
            channel1.enableLights(false)
            channel1.vibrationPattern = longArrayOf(0L)
            if (this::notificationManager.isInitialized) {
                notificationManager.createNotificationChannel(channel1)
            }
        }
    }

    inner class MusicPlayerNotificationBinder : Binder() {
        internal val service: MusicPlayerNotification
            get() = this@MusicPlayerNotification
    }

    companion object {
        const val NOTIFICATION_MUSIC_PLAYER_ID = 1
        const val CHANNEL_1_ID = "musicPlayerNotificationChannel1"
    }
}
