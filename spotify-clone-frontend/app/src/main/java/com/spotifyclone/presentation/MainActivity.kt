package com.spotifyclone.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.spotifyclone.R
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.include_bottom_navigation_menu.*


class MainActivity : AppCompatActivity() {
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

        val activityTabAdapter = PageTabAdapter(this, 3)
        tabViewPager.adapter = activityTabAdapter

        tabViewPager.registerOnPageChangeCallback(pageChangeCallback)

        bottomNavMenu.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
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


//        val intent: Intent = if (DesenvolUtils.appInDesenvol()) {
//            DesenvolUtils.getActivityBeingTested(context)
//        } else {
//            if (UserSession.getUserStatus() == UserSession.USER_LOGGED) {
//                Intent(context, HomeActivity::class.java)
//            } else {
//                Intent(context, LoginActivity::class.java)
//            }
//        }
//
//        context.startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        tabViewPager.unregisterOnPageChangeCallback(pageChangeCallback)
    }


}
