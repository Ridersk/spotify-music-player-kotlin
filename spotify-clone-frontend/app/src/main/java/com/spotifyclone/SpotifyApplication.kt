package com.spotifyclone

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.spotifyclone.presentation.notifications.MusicPlayerNotification

class SpotifyApplication : Application(), LifecycleObserver {

    private var visibilityChangeListener: ValueChangeListener? = null

    override fun onCreate() {
        super.onCreate()
        mInstance = this

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnterForeground() {
        isAppInBackground(false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnterBackground() {
        isAppInBackground(true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        isAppInBackground(true)
    }

    fun setOnVisibilityChangeListener(listener: ValueChangeListener) {
        this.visibilityChangeListener = listener
    }

    private fun isAppInBackground(isBackground: Boolean?) {
        visibilityChangeListener?.onChanged(isBackground)
    }

    interface ValueChangeListener {
        fun onChanged(value: Boolean?)
    }


    fun startMusicPlayerNotification() {
        val intent = Intent(applicationContext, MusicPlayerNotification::class.java)
        applicationContext?.startService(intent)
        applicationContext?.bindService(
            intent,
            mServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }


    companion object {
        var mInstance: SpotifyApplication? = null
        var mBoundService: MusicPlayerNotification? = null
        var mServiceBound = false

        val mServiceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName) {
                mServiceBound = false
            }

            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val myBinder = service as MusicPlayerNotification.MusicPlayerNotificationBinder
                mBoundService = myBinder.service
                mServiceBound = true
            }
        }

        fun getInstance(): SpotifyApplication? {
            return mInstance
        }
    }
}
