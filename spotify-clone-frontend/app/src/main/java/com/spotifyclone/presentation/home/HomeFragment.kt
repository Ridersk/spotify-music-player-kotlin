package com.spotifyclone.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.BaseScreenFragment
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.presentation.main.IWrapperFragment
import com.spotifyclone.presentation.playlist.LocalSongsFragment
import kotlinx.android.synthetic.main.fragment_home_page.*
import kotlinx.android.synthetic.main.fragment_home_page.view.*

class HomeFragment private constructor(private val parentActivity: BaseActivity) : BaseScreenFragment(parentActivity) {
    private lateinit var mListener: IWrapperFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mListener = this.parentFragment as IWrapperFragment
        return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

    override fun initComponents() {
        val layout: ViewGroup = activityHome
        val viewModel: RecommendedPlaylistsViewModel = RecommendedPlaylistsViewModel
            .ViewModelFactory().create(RecommendedPlaylistsViewModel::class.java)
        val parentActivity = this.parentActivity

        layout.labelRecommendedPlaylists.text = getString(R.string.fragment_home_label_recommendedPlaylists)
        viewModel.recommendedPlaylistsLiveData.observe(this, Observer {
            it?.let { playlists ->
                with(recommendedPlaylistGrid) {
                    adapter = RecommendedPlaylistsAdapter(playlists) { playlist ->
                        val playlistFragment =
                            LocalSongsFragment.getInstance(parentActivity, "Test")
                        val args = Bundle()
                        mListener.onReplace(playlistFragment, args)
                    }
                }
            }
        })
        viewModel.getRecommendedPlaylists()
    }

    override fun getToolbar(): ToolbarParameters =
        ToolbarParameters(
            option3 = Pair(R.drawable.ic_settings, {})
        )

    companion object {
        fun getInstance(parent: BaseActivity): Fragment {
            val homeFragment = HomeFragment(parent)
            val bundle = Bundle()
            homeFragment.arguments = bundle
            return homeFragment
        }
    }
}