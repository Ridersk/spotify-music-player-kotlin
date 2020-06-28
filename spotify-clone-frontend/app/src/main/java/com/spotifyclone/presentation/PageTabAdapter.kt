package com.spotifyclone.presentation

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.spotifyclone.presentation.home.HomeFragment

class PageTabAdapter(private val activity: AppCompatActivity, private val itemsCount: Int) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return itemsCount
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            PAGE_HOME -> HomeFragment.getInstance(activity)
            else -> HomeFragment.getInstance(activity)
        }
    }

    companion object {
        private const val PAGE_HOME = 0
        private const val PAGE_SEARCH = 1
        private const val PAGE_LIBRARY = 3
    }
}