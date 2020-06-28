package com.spotifyclone.presentation.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.spotifyclone.R
import com.spotifyclone.presentation.playlist.LocalSongsActivity
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.android.synthetic.main.fragment_home_page.view.*

class HomeFragment(private val parentContext: Context): Fragment() {

    companion object {
        fun getInstance(context: Context): Fragment {
            val activityTabFragment = HomeFragment(context)
            val bundle = Bundle()
            activityTabFragment.arguments = bundle
            return activityTabFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val layout: ViewGroup = activityHome
        setRecommendedPlaylistsGridHomeFragment(layout)
    }

    private fun setRecommendedPlaylistsGridHomeFragment(layout: ViewGroup) {
        val viewModel: RecommendedPlaylistsViewModel = RecommendedPlaylistsViewModel
            .ViewModelFactory().create(RecommendedPlaylistsViewModel::class.java)

        layout.labelRecommendedPlaylists.text = getString(R.string.home_label_recommendedPlaylists)
        viewModel.recommendedPlaylistsLiveData.observe(this, Observer {
            it?.let { playlists ->
                with(recommendedPlaylistGrid) {
                    adapter = RecommendedPlaylistsAdapter(playlists) { playlist ->
                        val intent =
                            LocalSongsActivity.getStartIntent(parentContext, playlist.title)
                        parentContext.startActivity(intent)
                    }
                }
            }
        })


        viewModel.getRecommendedPlaylists()
    }
}