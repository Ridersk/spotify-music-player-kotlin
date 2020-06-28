package com.spotifyclone.presentation

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.home.HomeFragment
import com.spotifyclone.presentation.playlist.LocalSongsFragment

class PageTabAdapter(private val activity: BaseActivity, private val itemsCount: Int) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            PAGE_HOME -> HomeFragment.getInstance(activity)
            PAGE_LIBRARY -> LocalSongsFragment.getInstance(activity, "Teste")
            else -> HomeFragment.getInstance(activity)
        }
    }

    companion object {
        private const val PAGE_HOME = 0
        private const val PAGE_SEARCH = 1
        private const val PAGE_LIBRARY = 2
    }
}