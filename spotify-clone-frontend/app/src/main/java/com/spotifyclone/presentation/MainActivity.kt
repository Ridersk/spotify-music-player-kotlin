package com.spotifyclone.presentation

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.login.LoginActivity
import com.spotifyclone.presentation.playlist.LocalSongsFragment
import com.spotifyclone.tools.session.UserSession
import kotlinx.android.synthetic.main.include_bottom_navigation_menu.*


class MainActivity : BaseActivity() {
    val context = this@MainActivity
    var pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            Toast.makeText(this@MainActivity, "Selected position: $position", Toast.LENGTH_SHORT)
                .show()
            super.onPageSelected(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (UserSession.getUserStatus() == UserSession.USER_LOGGED) {
            initMainActivity()
        } else {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    private fun initMainActivity() {
        val activityTabAdapter = PageTabAdapter(this, 3)
        tabViewPager.adapter = activityTabAdapter

        tabViewPager.registerOnPageChangeCallback(pageChangeCallback)

        bottomNavMenu.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page1 -> {
                    tabViewPager.currentItem = 0
                    true
                }
                R.id.page2 -> {
                    tabViewPager.currentItem = 1
                    true
                }
                R.id.page3 -> {
                    tabViewPager.currentItem = 2
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabViewPager.unregisterOnPageChangeCallback(pageChangeCallback)
    }


}
