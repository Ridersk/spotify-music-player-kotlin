package com.spotifyclone.presentation.music

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.spotifyclone.data.model.Music

class ItemMusicPlayerFragmentAdapter(
    activity: FragmentActivity,
    private val containerViewPager: ViewPager2,
    private var showMusic: Music,
    private val onClickCallback: () -> Unit
) : FragmentStateAdapter(activity) {

    private val musicList = mutableListOf(Music(), showMusic, Music())

    fun update(music: Music?) {
        if (music != null) {
            this.showMusic = music
            this.musicList[VIEW_VISIBLE] = this.showMusic
        }
        containerViewPager.setCurrentItem(VIEW_VISIBLE, false)
        super.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun getItemId(position: Int): Long {
        return musicList[position].hashCode().toLong()
    }

    override fun createFragment(position: Int): Fragment {
        if (position == VIEW_VISIBLE) {
            return ItemMusicPlayerFragment.getInstance(
                musicList[position].title,
                musicList[position].artist,
                true,
                onClickCallback
            )
        }
        return ItemMusicPlayerFragment.getInstance(
            null,
            null,
            false
        ) {}
    }

    companion object {
        const val VIEW_VISIBLE = 1
    }
}
