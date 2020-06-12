package com.spotifyclone.tools.musicplayer

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Handler
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import com.spotifyclone.tools.basepatterns.SingletonHolder

class SpotifyAudioManager private constructor(var context: Context) {

    private val focusLock = Any()
    private var playbackDelayed: Boolean = false
    private var playbackNowAuthorized: Boolean = false
    private var resumeOnFocusGain: Boolean = false
    private val musicAttributes: AudioAttributesCompat

    init {
        musicAttributes = getMusicAttributes()
    }

    fun startMusic(callPlayback: () -> Unit = {}, callPauseback: () -> Unit = {}) {
        if (playbackNowAuthorized) {
            callPlayback.invoke()
            return
        }

        val audioManager: AudioManager = getAudioService()
        val afChangeListener: AudioManager.OnAudioFocusChangeListener = getAfterChangeListener(callPlayback, callPauseback)
        val focusRequest: AudioFocusRequestCompat = buildFocusRequest(afChangeListener)

        playbackNowAuthorized = requestFocus(audioManager, focusRequest, callPlayback)
    }

    private fun requestFocus(
        audioManager: AudioManager,
        focusRequest: AudioFocusRequestCompat,
        callPlayback: () -> Unit = {}):Boolean {
        val res = AudioManagerCompat.requestAudioFocus(audioManager, focusRequest)
        val authorization: Boolean

        synchronized(focusLock) {
            authorization = when (res) {
                AudioManager.AUDIOFOCUS_REQUEST_FAILED -> false
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                    callPlayback.invoke()
                    true
                }
                AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
                    playbackDelayed = true
                    false
                }
                else -> false
            }
        }

        return authorization
    }

    private fun buildFocusRequest(
        afChangeListener: AudioManager.OnAudioFocusChangeListener,
        handler: Handler = Handler()
    ): AudioFocusRequestCompat {
        return AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN).run {
            setAudioAttributes(musicAttributes)
            setOnAudioFocusChangeListener(afChangeListener, handler)
            build()
        }
    }

    private fun getAfterChangeListener(
        callPlayback: () -> Unit,
        callPauseback: () -> Unit
    ): AudioManager.OnAudioFocusChangeListener {
        return AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN ->
                    if (playbackDelayed || resumeOnFocusGain) {
                        synchronized(focusLock) {
                            playbackDelayed = false
                            resumeOnFocusGain = false
                        }
                        callPlayback.invoke()
                    }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    synchronized(focusLock) {
                        resumeOnFocusGain = false
                        playbackDelayed = false
                        playbackNowAuthorized = false
                    }
                    callPauseback.invoke() // problem loss focus
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    synchronized(focusLock) {
                        resumeOnFocusGain = true
                        playbackDelayed = false
                    }
                    callPauseback.invoke()
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    // ... pausing or ducking
                    callPlayback.invoke()
                }
            }
        }
    }

    private fun getAudioService(): AudioManager {
        return context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private fun getMusicAttributes(): AudioAttributesCompat {
        return AudioAttributesCompat.Builder().run {
            setUsage(AudioAttributesCompat.USAGE_MEDIA)
            setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
            build()
        }
    }

    companion object : SingletonHolder<SpotifyAudioManager, Context>(::SpotifyAudioManager)
}