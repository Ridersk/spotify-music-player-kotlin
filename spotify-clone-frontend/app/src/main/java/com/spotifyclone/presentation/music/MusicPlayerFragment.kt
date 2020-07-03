package com.spotifyclone.presentation.music

import android.content.Context
import android.os.Bundle
import android.text.SpannedString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.presentation.base.BaseFragment
import com.spotifyclone.tools.basepatterns.SingletonHolder
import com.spotifyclone.tools.musicplayer.MusicObserver
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import com.spotifyclone.tools.utils.ImageUtils
import kotlinx.android.synthetic.main.fragment_music_player.*

class MusicPlayerFragment private constructor(private val parentContext: Context) : BaseFragment(),
    MusicObserver {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(parentContext)

    init {
        playlistMusicPlayer.addMusicObserver(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music_player, container, false)
    }

    override fun initComponents() {
        val label: SpannedString = buildSpannedString {
            append(requireArguments().getString(EXTRA_MUSIC_TITLE))
            color(ContextCompat.getColor(requireContext(), R.color.lightGray)) {
                append(" • ${requireArguments().getString(EXTRA_MUSIC_ARTIST)}")
            }
        }
        txtMusicLabel.text = label
        ImageUtils.insertBitmapInView(
            activity!!.applicationContext,
            imgAlbum,
            requireArguments().getLong(EXTRA_ALBUM_URI_ID, -1)
        )

        btnPlay.isActivated = playlistMusicPlayer.isPlaying
        btnPlay.setOnClickListener {
            playlistMusicPlayer.tooglePlayMusic()
            btnPlay.isActivated = playlistMusicPlayer.isPlaying
        }

        playlistMusicPlayer.setObserverOnMusicState(parentContext) {
            btnPlay.isActivated = playlistMusicPlayer.isPlaying
        }

        playlistMusicPlayer.setObserverProgressBar(parentContext) { progress: Int ->
            progressBar.progress = progress
        }
    }

    override fun changedMusic(music: Music) {
        reload(music)
    }

    private fun reload(music: Music) {
        arguments?.apply {
            putString(EXTRA_MUSIC_TITLE, music.title)
            putString(EXTRA_MUSIC_ARTIST, music.artist)
            putLong(EXTRA_ALBUM_URI_ID, music.albumUriId)
        }
        initComponents()
    }

    companion object : SingletonHolder<MusicPlayerFragment, Context>(::MusicPlayerFragment) {
        private const val EXTRA_MUSIC_TITLE = "EXTRA_MUSIC_TITLE"
        private const val EXTRA_MUSIC_ARTIST = "EXTRA_MUSIC_ARTIST"
        private const val EXTRA_ALBUM_URI_ID = "EXTRA_ALBUM_URI_ID"

        fun getInstance(
            context: Context,
            musicTitle: String,
            musicArtist: String,
            albumUrId: Long
        ): MusicPlayerFragment {
            val fragment = this.getInstance(context)
            val bundle = Bundle()
            bundle.putString(EXTRA_MUSIC_TITLE, musicTitle)
            bundle.putString(EXTRA_MUSIC_ARTIST, musicArtist)
            bundle.putLong(EXTRA_ALBUM_URI_ID, albumUrId)
            fragment.arguments = bundle
            return fragment
        }
    }
}