package com.spotifyclone.presentation.playlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_liked_songs.*

class LikedSongsActivity : BaseActivity(), PlaylistInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liked_songs)

        textTitle.text = getString(R.string.liked_text_title)
        buttonRandomPlay.text = getString(R.string.liked_button_random_play)
        textDownloadedSongs.text = getString(R.string.liked_text_downloaded_songs)

        setMusicList()
    }

    fun setMusicList() {
        val viewModel: PlaylistMusicsViewModel = PlaylistMusicsViewModel
            .ViewModelFactory().create(PlaylistMusicsViewModel::class.java)

        viewModel.musicsLiveData.observe(this, Observer {
            it?.let { musics ->
                with(recyclerMusics) {
                    layoutManager = LinearLayoutManager(this@LikedSongsActivity, RecyclerView.VERTICAL, false)
                    setHasFixedSize(true)
                    adapter = PlaylistMusicsAdapter(musics) {
                    }
                }
            }
        })

        viewModel.getMusics()
    }

    companion object {
        private const val EXTRA_TITLE = "EXTRA_TITLE"

        fun getStartIntent(context: Context, title: String) : Intent {
            return Intent(context, LikedSongsActivity::class.java).apply {
                putExtra(EXTRA_TITLE, title)
            }
        }

    }
}
