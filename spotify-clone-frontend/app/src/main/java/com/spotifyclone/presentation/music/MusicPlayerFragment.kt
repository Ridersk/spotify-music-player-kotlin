package com.spotifyclone.presentation.music

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.viewpager2.widget.ViewPager2
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.presentation.base.BaseFragment
import com.spotifyclone.tools.basepatterns.SingletonHolder
import com.spotifyclone.tools.musicplayer.MusicObserver
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import com.spotifyclone.tools.utils.ImageUtils
import kotlinx.android.synthetic.main.fragment_music_player.*
import java.util.*

class MusicPlayerFragment private constructor(
    private val parentContext: Context
) : BaseFragment(),
    MusicObserver {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(parentContext)
    private var musicPlaying: Music =  Music()
    private lateinit var itemMusicLabelAdapter: ItemMusicPlayerFragmentAdapter
    private val currentPosition = ItemMusicPlayerFragmentAdapter.VIEW_VISIBLE
    private lateinit var idCallbackStateMusic: UUID
    private val callbackViewPager = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            if (position != currentPosition) {
                if (position > currentPosition) {
                    playlistMusicPlayer.nextMusic()
                } else playlistMusicPlayer.previousMusic()
                super.onPageSelected(position)
            }
        }
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createMusicSliderViewPager()
        createCallbacks()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initComponents() {
        imgAlbum.setImageBitmap(
            ImageUtils.getBitmapAlbumArt(
                activity!!.applicationContext,
                requireArguments().getLong(EXTRA_ALBUM_URI_ID, -1)
            )
        )

        btnLike.setOnClickListener {
            btnLike.isActivated = !btnLike.isActivated
        }

        btnPlay.isActivated = playlistMusicPlayer.isPlaying
        btnPlay.setOnClickListener {
            playlistMusicPlayer.tooglePlayMusic()
            btnPlay.isActivated = playlistMusicPlayer.isPlaying
        }

        playlistMusicPlayer.setObserverProgressBar(parentContext) { progress: Int ->
            progressBar?.progress = progress
        }
    }

    override fun removeComponents() {
        playlistMusicPlayer.removeObserverOnMusicState(idCallbackStateMusic)
        playlistMusicPlayer.removeObserverProgressBar(parentContext)
    }

    private fun createCallbacks() {
        idCallbackStateMusic = playlistMusicPlayer.setObserverOnMusicState {
            btnPlay.isActivated = playlistMusicPlayer.isPlaying
        }
    }

    override fun changedMusic(music: Music) {
        reload(music)
    }

    private fun reload(music: Music) {
        arguments?.apply {
            putLong(EXTRA_ALBUM_URI_ID, music.albumUriId?:-1L)
        }
        initComponents()
        updateViewLabelMusic(music)
    }

    private fun createMusicSliderViewPager() {
        itemMusicLabelAdapter = ItemMusicPlayerFragmentAdapter(
            requireActivity(),
            containerMusicPlayerViewPager,
            this.musicPlaying,
            this::callOnClick
        )
        containerMusicPlayerViewPager.adapter = itemMusicLabelAdapter
        containerMusicPlayerViewPager.registerOnPageChangeCallback(callbackViewPager)
        updateViewLabelMusic(playlistMusicPlayer.getCurrentMusic())
    }

    private fun updateViewLabelMusic(music: Music?) {
        if (music != null) {
            if (fragmentMusicPlayer.visibility != View.VISIBLE) {
                fragmentMusicPlayer.visibility = View.VISIBLE
            }
            fragmentMusicPlayer.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.bottom_to_top))
        } else fragmentMusicPlayer.visibility = View.GONE
        containerMusicPlayerViewPager.post {
            itemMusicLabelAdapter.update(music)
        }
    }

    private fun callOnClick() {
        fragmentMusicPlayer.callOnClick()
    }

    companion object : SingletonHolder<MusicPlayerFragment, Context>(::MusicPlayerFragment) {
        private const val EXTRA_ALBUM_URI_ID = "EXTRA_ALBUM_URI_ID"

        fun getInstanceFragment(
            context: Context,
            albumUrId: Long = -1
        ): MusicPlayerFragment {
            val fragment = this.getInstance(context)
            val bundle = Bundle()
            bundle.putLong(EXTRA_ALBUM_URI_ID, albumUrId)
            fragment.arguments = bundle
            return fragment
        }
    }
}
