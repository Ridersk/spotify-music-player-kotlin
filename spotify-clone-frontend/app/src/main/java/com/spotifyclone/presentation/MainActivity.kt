package com.spotifyclone.presentation

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import com.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.presentation.login.LoginActivity
import com.spotifyclone.presentation.main.PageTabAdapter
import com.spotifyclone.tools.session.UserSession
import kotlinx.android.synthetic.main.include_bottom_navigation_menu.*

class MainActivity : BaseActivity() {

    private lateinit var tabAdapter: PageTabAdapter
    val context = this@MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)

        super.setupToolbar(ToolbarParameters())

        if (UserSession.getUserStatus() == UserSession.USER_LOGGED) {
            initMainViewTabs()
        } else {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        if (!tabAdapter.onBackPressed()) {
            super.onBackPressed()
        }
    }

    private fun initMainViewTabs() {
        tabAdapter = PageTabAdapter(this, containerViewPager)
        containerViewPager.adapter = tabAdapter
        containerViewPager.isUserInputEnabled = false

        bottomNavMenu.setOnNavigationItemSelectedListener { item: MenuItem -> tabAdapter.selectTab(item)}
    }
}
