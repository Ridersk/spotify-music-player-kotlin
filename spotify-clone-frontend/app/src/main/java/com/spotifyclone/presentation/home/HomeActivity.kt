package com.spotifyclone.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.presentation.playlist.LocalSongsActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.include_toolbar.*

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_home)


        setupToolbar(
            ToolbarParameters(
                toolbar =  toolbarMain,
                title =  getString(R.string.home_toolbar_title),
                option3 =  Pair(R.drawable.ic_settings, {})
            )
        )

        super.onCreate(savedInstanceState)
    }

    override fun initComponents() {
        val layout: ViewGroup = activityHome
        setRecommendedPlaylistsGrid(layout)
    }

    override fun onBackPressed() {
        val mainIntent = Intent(Intent.ACTION_MAIN)
        mainIntent.addCategory(Intent.CATEGORY_HOME)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(mainIntent)
    }

    private fun setRecommendedPlaylistsGrid(layout: ViewGroup) {
        val viewModel: RecommendedPlaylistsViewModel = RecommendedPlaylistsViewModel
            .ViewModelFactory().create(RecommendedPlaylistsViewModel::class.java)

        layout.labelRecommendedPlaylists.text = getString(R.string.home_label_recommendedPlaylists)
        viewModel.recommendedPlaylistsLiveData.observe(this, Observer {
            it?.let { playlists ->
                with(recommendedPlaylistGrid) {
                    adapter = RecommendedPlaylistsAdapter(playlists) { playlist ->
                        val intent =
                            LocalSongsActivity.getStartIntent(this@HomeActivity, playlist.title)
                        this@HomeActivity.startActivity(intent)
                    }
                }
            }
        })


        viewModel.getRecommendedPlaylists()
    }
}