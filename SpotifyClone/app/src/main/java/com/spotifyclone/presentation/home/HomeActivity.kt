package com.spotifyclone.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.presentation.playlist.LikedSongsActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.include_toolbar.*

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        setupToolbar(
            ToolbarParameters(
                toolbar =  toolbarMain,
                title =  getString(R.string.home_toolbar_title),
                option3 =  Pair(R.drawable.ic_settings, {})
            )
        )

        val layout: ViewGroup = activityHome
        setRecommendedPlaylistsGrid(layout)
    }

    override fun onBackPressed() {
        val mainIntent = Intent(Intent.ACTION_MAIN)
        mainIntent.addCategory(Intent.CATEGORY_HOME)
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(mainIntent)
    }

    fun setRecommendedPlaylistsGrid(layout: ViewGroup) {
        val viewModel: RecommendedPlaylistsViewModel = RecommendedPlaylistsViewModel
            .ViewModelFactory().create(RecommendedPlaylistsViewModel::class.java)

        layout.labelRecommendedPlaylists.text = getString(R.string.home_label_recommendedPlaylists)
        viewModel.recommendedPlaylistsLiveData.observe(this, Observer {
            it?.let { playlists ->
                with(recommendedPlaylistGrid) {
                    adapter = RecommendedPlaylistsAdapter(playlists) { playlist ->
                        val intent =
                            LikedSongsActivity.getStartIntent(this@HomeActivity, playlist.title)
                        this@HomeActivity.startActivity(intent)
                    }
                }
            }
        })


        viewModel.getRecommendedPlaylists()
    }
}