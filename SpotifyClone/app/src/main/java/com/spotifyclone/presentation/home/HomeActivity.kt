package com.spotifyclone.presentation.home

import android.os.Bundle
import androidx.lifecycle.Observer
import com.example.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        setRecommendedPlaylistsGrid()
    }

    fun setRecommendedPlaylistsGrid() {
        val viewModel: RecommendedPlaylistsViewModel = RecommendedPlaylistsViewModel
            .ViewModelFactory().create(RecommendedPlaylistsViewModel::class.java)

        labelRecommendedPlaylists.text = getString(R.string.home_label_recommendedPlaylists)
        viewModel.recommendedPlaylistsLiveData.observe(this, Observer {
            it?.let { playlists ->
                with(recommendedPlaylistGrid) {
                    adapter = RecommendedPlaylistsAdapter(playlists) {playlist ->
                        println("Playlist Invoke")
                    }
                }
            }
        })


        viewModel.getRecommendedPlaylists()
    }
}