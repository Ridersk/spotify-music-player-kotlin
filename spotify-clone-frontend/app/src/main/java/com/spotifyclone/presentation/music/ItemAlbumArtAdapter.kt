package com.spotifyclone.presentation.music

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class ItemAlbumArtAdapter(
    activity: FragmentActivity,
    private val albumArtList: List<Long?>,
    private val containerViewPager: ViewPager2
) : FragmentStateAdapter(activity) {

    var currentPosition = 0

    override fun getItemCount(): Int {
        return albumArtList.size
    }

    override fun getItemId(position: Int): Long {
        return albumArtList[position].hashCode().toLong()
    }

    override fun createFragment(position: Int): Fragment {
        return ItemAlbumArtFragment.getInstance(albumArtList[position])
    }

    fun update(position: Int, showAnimation: Boolean = true) {
        currentPosition = position
        containerViewPager.setCurrentItem(position, showAnimation)

        super.notifyDataSetChanged()
    }
}
