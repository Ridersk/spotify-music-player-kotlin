package com.spotifyclone.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.spotifyclone.R
import com.spotifyclone.presentation.maintab.IWrapperFragment
import com.spotifyclone.presentation.playlist.LocalSongsFragment
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.android.synthetic.main.fragment_home_page.view.*

class HomeFragment : Fragment() {

    private lateinit var mListener: IWrapperFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mListener = this.parentFragment as IWrapperFragment
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
                        val playlistFragment = LocalSongsFragment.getInstance("Test")
                        val args = Bundle()
                        mListener.onReplace(playlistFragment, args)
                    }
                }
            }
        })
        viewModel.getRecommendedPlaylists()
    }

    companion object {
        fun getInstance(): Fragment {
            val homeFragment = HomeFragment()
            val bundle = Bundle()
            homeFragment.arguments = bundle
            return homeFragment
        }
    }
}