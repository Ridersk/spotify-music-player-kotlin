package com.spotifyclone.presentation.main

import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.home.HomeFragment
import com.spotifyclone.presentation.library.LibraryFragment
import com.spotifyclone.presentation.search.SearchFragment

class PageTabAdapter(activity: BaseActivity, private val containerViewPager: ViewPager2) :
    FragmentStateAdapter(activity) {

    private val tabs = mutableListOf<TabWrapperFragment>()
    private var currentTab = 0

    init {
        tabs.add(TabWrapperFragment.getInstance(HomeFragment.getInstance(activity)))
        tabs.add(TabWrapperFragment.getInstance(SearchFragment.getInstance(activity)))
        tabs.add(TabWrapperFragment.getInstance(LibraryFragment.getInstance(activity)))
    }

    override fun getItemCount(): Int {
        return NUMBER_OF_PAGES
    }

    override fun createFragment(position: Int): Fragment {
        return tabs[position]
    }

    fun selectTab(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.page1 -> {
                this.currentTab = 0
                this.tabs[this.currentTab].reset()
                containerViewPager.currentItem = this.currentTab
                true
            }
            R.id.page2 -> {
                this.currentTab = 1
                containerViewPager.currentItem = this.currentTab
                true
            }
            R.id.page3 -> {
                this.currentTab = 2
                containerViewPager.currentItem = currentTab
                true
            }
            else -> false
        }
    }

    fun onBackPressed(): Boolean {
        return tabs[currentTab].onBackPressed()
    }

    companion object {
        private const val NUMBER_OF_PAGES = 3
    }
}
