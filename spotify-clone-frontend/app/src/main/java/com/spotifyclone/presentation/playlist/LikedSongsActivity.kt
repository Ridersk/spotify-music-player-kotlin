package com.spotifyclone.presentation.playlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.presentation.music.MusicPlayerActivity
import com.spotifyclone.tools.musicplayer.PlaylistController
import com.spotifyclone.tools.musicplayer.PlaylistObserverProvider
import com.spotifyclone.tools.musicplayer.PlaylistObserver
import kotlinx.android.synthetic.main.activity_liked_songs.*
import kotlinx.android.synthetic.main.activity_liked_songs.view.*
import kotlinx.android.synthetic.main.include_toolbar.*

class LikedSongsActivity : BaseActivity(), PlaylistInterface, PlaylistObserver<Music> {

    private val playlistController = PlaylistController.getInstance(this@LikedSongsActivity)

    lateinit var layout: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_liked_songs)

        setupToolbar(
            ToolbarParameters(
                toolbar = toolbarMain,
                title = intent.getStringExtra(EXTRA_TITLE),
                option1 = Pair(R.drawable.ic_back, { super.onBackPressed() }),
                option3 = Pair(R.drawable.ic_options, {})
            )
        )

        super.onCreate(savedInstanceState)
    }

    override fun initComponents() {
        layout = activityLikedSongs
        with(layout) {
            textTitle.text = intent.getStringExtra(EXTRA_TITLE)
            buttonRandomPlay.text = getString(R.string.liked_button_random_play)
            textDownloadedSongs.text = getString(R.string.liked_text_downloaded_songs)
        }

        setMusicList()
    }

    override fun getPlaylistName() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun receiverList(list: List<Music>) {
        with(layout.recyclerMusics) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                this@LikedSongsActivity,
                androidx.recyclerview.widget.RecyclerView.VERTICAL,
                false
            )
            setHasFixedSize(true)
            adapter =
                PlaylistMusicsAdapter(list) { music, position ->
                    val intent =
                        MusicPlayerActivity.getStartIntent(
                            this@LikedSongsActivity,
                            music.title,
                            music.artist,
                            music.albumUriId,
                            getString(EXTRA_PLAYLIST_NAME)
                        )

                    chooseItem(position)
                    this@LikedSongsActivity.startActivity(intent)
                }
        }
    }

    override fun chooseItem(position: Int) {
        playlistController.chooseItem(position)
    }

    private fun setMusicList() {
        val viewModel: PlaylistMusicsViewModel = PlaylistMusicsViewModel
            .ViewModelFactory(this@LikedSongsActivity)
            .create(PlaylistMusicsViewModel::class.java)

        val playlistObserverProvider = PlaylistObserverProvider()

        playlistObserverProvider.addReceiver(playlistController)
        playlistObserverProvider.addReceiver(this)
        viewModel.musicsLiveData.observe(this, playlistObserverProvider)

        viewModel.getMusics()
    }

    companion object {
        private const val EXTRA_PLAYLIST_NAME: Int = R.string.liked_playlist_title
        private const val EXTRA_TITLE = "EXTRA_TITLE"

        fun getStartIntent(context: Context, title: String): Intent {
            return Intent(context, LikedSongsActivity::class.java).apply {
                putExtra(EXTRA_TITLE, title)
            }
        }

    }
}
