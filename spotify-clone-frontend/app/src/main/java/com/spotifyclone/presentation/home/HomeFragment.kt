package com.spotifyclone.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.BaseScreenFragment
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.presentation.main.IWrapperFragment
import com.spotifyclone.presentation.playlist.LocalSongsFragment
import kotlinx.android.synthetic.main.fragment_page_home.*
import kotlinx.android.synthetic.main.fragment_page_home.view.*

class HomeFragment private constructor(private val parentActivity: BaseActivity) :
    BaseScreenFragment(parentActivity) {
    private lateinit var mListener: IWrapperFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mListener = this.parentFragment as IWrapperFragment
        return inflater.inflate(R.layout.fragment_page_home, container, false)
    }

    override fun getToolbar(): ToolbarParameters =
        ToolbarParameters(
            option3 = Pair(R.drawable.ic_settings, {})
        )

    override fun initComponents() {
        val layout: ViewGroup = fragmentHome
        val viewModel: RecommendedPlaylistsViewModel = RecommendedPlaylistsViewModel
            .ViewModelFactory(context!!).create(RecommendedPlaylistsViewModel::class.java)
        val parentActivity = this.parentActivity

        layout.labelRecommendedPlaylists.text =
            getString(R.string.fragment_home_label_recommendedPlaylists)
        viewModel.recommendedPlaylistsLiveData.observe(this, Observer {
            it?.let { playlists ->
                with(recommendedPlaylistGrid) {
                    adapter = RecommendedPlaylistsAdapter(context, playlists) { playlist ->
                        val playlistFragment =
                            LocalSongsFragment.getInstance(
                                parentActivity,
                                playlist.layoutFragment
                            )
                        mListener.onReplace(playlistFragment)
                    }
                }
            }
        })
        viewModel.getRecommendedPlaylists()
    }

    companion object {
        fun getInstance(parent: BaseActivity): HomeFragment {
            val homeFragment = HomeFragment(parent)
            val bundle = Bundle()
            homeFragment.arguments = bundle
            return homeFragment
        }
    }
}
