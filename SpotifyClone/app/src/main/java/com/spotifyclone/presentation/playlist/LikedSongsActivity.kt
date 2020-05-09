package com.spotifyclone.presentation.playlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.presentation.music.MusicPlayerActivity
import kotlinx.android.synthetic.main.activity_liked_songs.*
import kotlinx.android.synthetic.main.activity_liked_songs.view.*
import kotlinx.android.synthetic.main.include_toolbar.*

class LikedSongsActivity : BaseActivity(), PlaylistInterface {


    override fun getPlaylistName() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liked_songs)

        setupToolbar(
            ToolbarParameters(
                toolbar = toolbarMain,
                title = intent.getStringExtra(EXTRA_TITLE),
                option1 = Pair(R.drawable.ic_back, { super.onBackPressed() }),
                option3 = Pair(R.drawable.ic_options, {})
            )
        )

        val layout: ViewGroup = activityLikedSongs
        with(layout) {
            textTitle.text = intent.getStringExtra(EXTRA_TITLE)
            buttonRandomPlay.text = getString(R.string.liked_button_random_play)
            textDownloadedSongs.text = getString(R.string.liked_text_downloaded_songs)
        }

        setMusicList(layout)
    }

    fun setMusicList(layout: ViewGroup) {
        val viewModel: PlaylistMusicsViewModel = PlaylistMusicsViewModel
            .ViewModelFactory().create(PlaylistMusicsViewModel::class.java)

        viewModel.musicsLiveData.observe(this, Observer {
            it?.let { musics ->
                with(layout.recyclerMusics) {
                    layoutManager =
                        LinearLayoutManager(this@LikedSongsActivity, RecyclerView.VERTICAL, false)
                    setHasFixedSize(true)
                    adapter = PlaylistMusicsAdapter(musics) { music ->
                        val intent = MusicPlayerActivity.getStartIntent(
                            this@LikedSongsActivity,
                            music.name,
                            music.author,
                            getString(PLAYLIST_NAME)
                        )

                        this@LikedSongsActivity.startActivity(intent)
                    }
                }
            }
        })


        viewModel.getMusics()
    }

    companion object {
        private val PLAYLIST_NAME: Int = R.string.liked_playlist_title
        private const val EXTRA_TITLE = "EXTRA_TITLE"

        fun getStartIntent(context: Context, title: String): Intent {
            return Intent(context, LikedSongsActivity::class.java).apply {
                putExtra(EXTRA_TITLE, title)
            }
        }

    }
}
