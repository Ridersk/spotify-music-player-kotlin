package com.spotifyclone.tools.musicplayer

import android.content.Context
import android.media.AudioManager
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import com.spotifyclone.tools.basepatterns.SingletonHolder

class SpotifyAudioManager private constructor(var context: Context) {

    private lateinit var audioManager: AudioManager
    private lateinit var focusRequest: AudioFocusRequestCompat

    private val focusLock = Any()
    private var playbackDelayed: Boolean = false
    private var playbackNowAuthorized: Boolean = false
    private var resumeOnFocusGain: Boolean = false

    fun startMusic(callPlayback: () -> Unit = {}, callPauseback: () -> Unit = {}) {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val afChangeListener: AudioManager.OnAudioFocusChangeListener = getAfterChangeListener(callPlayback, callPauseback)
        val musicAttributes: AudioAttributesCompat = getMusicAttributes()

        focusRequest = AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN).run {
            setAudioAttributes(musicAttributes)
            setOnAudioFocusChangeListener(afChangeListener)
            build()
        }


        val res = AudioManagerCompat.requestAudioFocus(audioManager, focusRequest)
        synchronized(focusLock) {
            playbackNowAuthorized = when (res) {
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
    }

    private fun getMusicAttributes(): AudioAttributesCompat {
        return AudioAttributesCompat.Builder().run {
            setUsage(AudioAttributesCompat.USAGE_MEDIA)
            setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
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
                    }
                    callPauseback.invoke()
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
                }
            }
        }
    }

    companion object : SingletonHolder<SpotifyAudioManager, Context>(::SpotifyAudioManager)
}