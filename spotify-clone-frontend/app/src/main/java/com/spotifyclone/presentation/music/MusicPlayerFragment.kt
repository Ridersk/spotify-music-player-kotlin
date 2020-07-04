package com.spotifyclone.presentation.music

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.presentation.base.BaseFragment
import com.spotifyclone.tools.basepatterns.SingletonHolder
import com.spotifyclone.tools.musicplayer.MusicObserver
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import com.spotifyclone.tools.utils.ImageUtils
import kotlinx.android.synthetic.main.fragment_music_player.*

class MusicPlayerFragment private constructor(
    private val parentContext: Context
) : BaseFragment(),
    MusicObserver {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(parentContext)
    private var musicList = mutableListOf<Music>()
    private lateinit var itemsMusicLabelAdapter: ItemsMusicPlayerFragmentAdapter
    private var currentPosition = 0
    private val callbackViewPager = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            if (position > currentPosition) {
                playlistMusicPlayer.nextMusic()
            } else playlistMusicPlayer.previousMusic()
            currentPosition = position
            super.onPageSelected(position)
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
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initComponents() {
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
            progressBar?.progress = progress
        }
    }

    override fun changedMusic(music: Music) {
        reload(music)
    }

    override fun updatedList(newMusicList: List<Music>) {
        updateMusicSliderViewPager(newMusicList)
    }

    private fun reload(music: Music) {
        arguments?.apply {
            putLong(EXTRA_ALBUM_URI_ID, music.albumUriId)
        }
        initComponents()
        updateMusicSliderViewPager(playlistMusicPlayer.getCompleteListInContext().toMutableList())
    }

    private fun updateMusicSliderViewPager(newMusicList: List<Music>) {
        if (this.musicList != newMusicList) {
            this.musicList.clear()
            this.musicList.addAll(newMusicList)
        }

//        containerMusicPlayerViewPager.post {
            itemsMusicLabelAdapter.notifyDataSetChanged()
//        }
    }

    private fun createMusicSliderViewPager() {
        musicList = playlistMusicPlayer.getCompleteListInContext().toMutableList()
        itemsMusicLabelAdapter = ItemsMusicPlayerFragmentAdapter(
            requireActivity(),
            this.musicList,
            this::callOnClick
        )
        containerMusicPlayerViewPager.adapter = itemsMusicLabelAdapter
        containerMusicPlayerViewPager.registerOnPageChangeCallback(callbackViewPager)
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