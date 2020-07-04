package com.spotifyclone.presentation.music

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.spotifyclone.data.model.Music

class ItemsMusicPlayerFragmentAdapter(
    activity: FragmentActivity,
    private val musicList: List<Music>,
    private val onClickCallback: () -> Unit
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun createFragment(position: Int): Fragment {
        return ItemMusicPlayerFragment.getInstance(
            musicList[position].title,
            musicList[position].artist,
            onClickCallback
        )
    }
}