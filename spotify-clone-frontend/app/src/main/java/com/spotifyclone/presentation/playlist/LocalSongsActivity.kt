package com.spotifyclone.presentation.playlist

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.data.repository.PlaylistMusicsDataSourceLocal
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.presentation.music.MusicPlayerActivity
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import com.spotifyclone.tools.musicplayer.PlaylistObserverProvider
import com.spotifyclone.tools.musicplayer.PlaylistObserver
import kotlinx.android.synthetic.main.activity_playlist.*
import kotlinx.android.synthetic.main.activity_playlist.view.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.util.*

class LocalSongsActivity : BaseActivity(), PlaylistInterface, PlaylistObserver<Music> {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(this@LocalSongsActivity)
    private lateinit var layout: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_playlist)

        setupToolbar(
            ToolbarParameters(
                toolbar = toolbarMain,
                title = intent.getStringExtra(EXTRA_TITLE),
                option1 = Pair(R.drawable.ic_back, { super.onBackPressed() }),
                option3 = Pair(R.drawable.ic_options, {})
            )
        )

        super.addRequiredPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        super.addRequiredPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        super.addRequiredPermissionDialog(
            getString(R.string.dialog_alert_txt_permissions_title),
            getString(R.string.dialog_alert_txt_permissions_description)
        )
        super.callInitComponentsWithoutPermission = true
        super.onCreate(savedInstanceState)
    }

    override fun initComponents() {
        layout = activityLocalSongs
        with(layout) {
            textTitle.text = intent.getStringExtra(EXTRA_TITLE)
            buttonRandomPlay.text = getString(R.string.local_songs_button_random_play)
            textDownloadedSongs.visibility = View.INVISIBLE
            swicthDownloadedSongs.visibility = View.INVISIBLE
        }

        setMusicList()
    }

    override fun getPlaylistName() {}

    override fun receiverList(list: List<Music>) {
        with(layout.recyclerMusics) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                this@LocalSongsActivity,
                RecyclerView.VERTICAL,
                false
            )
            setHasFixedSize(true)
            adapter =
                PlaylistMusicsAdapter(list) { music ->
                    val intent =
                        MusicPlayerActivity.getStartIntent(
                            this@LocalSongsActivity,
                            music.title,
                            music.artist,
                            music.albumUriId,
                            getString(EXTRA_PLAYLIST_NAME)
                        )

                    chooseMusic(music.id)
                    this@LocalSongsActivity.startActivity(intent)
                }
        }
    }

    override fun chooseMusic(id: UUID) {
        playlistMusicPlayer.chooseMusic(id)
    }

    private fun setMusicList() {
        val viewModel: PlaylistMusicsViewModel = PlaylistMusicsViewModel
            .ViewModelFactory(PlaylistMusicsDataSourceLocal(this))
            .create(PlaylistMusicsViewModel::class.java)

        val playlistObserverProvider = PlaylistObserverProvider()

        playlistObserverProvider.addReceiver(playlistMusicPlayer)
        playlistObserverProvider.addReceiver(this)

        viewModel.musicsLiveData.observe(this, playlistObserverProvider)
        viewModel.viewFlipperLiveData.observe(
            this,
            Observer { viewResult: ViewFlipperPlayslistMusics ->
                viewResult.let {
                    viewFlipperPlaylist.displayedChild = viewResult.showChild

                    viewResult.warningResId?.let { errorMessageResId ->
                        txtEmptySongList.text = getString(errorMessageResId)
                    }

                    viewResult.descriptionErrorResId?.let { descriptionErrorResId ->
                        txtDescriptionEmptySongList.text = getString(descriptionErrorResId)

                    }
                }
            })

        viewModel.getMusics()
    }

    companion object {
        private const val EXTRA_PLAYLIST_NAME: Int = R.string.local_songs_title
        private const val EXTRA_TITLE = "EXTRA_TITLE"

        fun getStartIntent(context: Context, title: String): Intent {
            return Intent(context, LocalSongsActivity::class.java).apply {
                putExtra(EXTRA_TITLE, title)
            }
        }

    }
}
