package com.spotifyclone.presentation

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import com.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.presentation.login.LoginActivity
import com.spotifyclone.presentation.main.PageTabAdapter
import com.spotifyclone.presentation.music.MusicPlayerActivity
import com.spotifyclone.presentation.music.MusicPlayerFragment
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import com.spotifyclone.tools.session.UserSession
import kotlinx.android.synthetic.main.fragment_music_player.*
import kotlinx.android.synthetic.main.include_bottom_navigation_menu.*

class MainActivity : BaseActivity() {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(this@MainActivity)
    private lateinit var tabAdapter: PageTabAdapter
    val context = this@MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)

        super.setupToolbar(ToolbarParameters())

        if (UserSession.getUserStatus() != UserSession.USER_LOGGED) {
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

    override fun initComponents() {
        createTabsMainView()
        createMusicPlayerFragment()
    }

    private fun createTabsMainView() {
        tabAdapter = PageTabAdapter(this, containerViewPager)
        containerViewPager.adapter = tabAdapter
        containerViewPager.isUserInputEnabled = false
        getString(R.string.dialog_alert_btn_permissions_cancel)
        bottomNavMenu.setOnNavigationItemSelectedListener { item: MenuItem ->
            tabAdapter.selectTab(
                item
            )
        }
    }

    private fun createMusicPlayerFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val musicPlayerFragment =
            MusicPlayerFragment.getInstanceFragment(this)
        fragmentTransaction.add(R.id.containerMusicPlayer, musicPlayerFragment)
        fragmentTransaction.commit()

    }

    fun onclickFragmentMusicPlayer(view: View) {
        if (view.id == R.id.fragmentMusicPlayer) {
            val music = playlistMusicPlayer.getCurrentMusic()
            val intent = MusicPlayerActivity.getIntent(
                context,
                music.title,
                music.artist,
                music.albumUriId,
                getString(R.string.fragment_local_songs_title)
            )
            this@MainActivity.startActivity(intent)
        }
    }
}
