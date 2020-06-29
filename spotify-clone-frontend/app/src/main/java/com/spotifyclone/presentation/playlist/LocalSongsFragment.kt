package com.spotifyclone.presentation.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.spotifyclone.R
import com.spotifyclone.data.model.Music
import com.spotifyclone.data.repository.PlaylistMusicsDataSourceLocal
import com.spotifyclone.presentation.music.MusicPlayerActivity
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import com.spotifyclone.tools.musicplayer.PlaylistObserverProvider
import com.spotifyclone.tools.musicplayer.PlaylistObserver
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.android.synthetic.main.fragment_playlist.view.*
import java.util.*

class LocalSongsFragment : Fragment(), PlaylistInterface,
    PlaylistObserver<Music> {

    private lateinit var playlistMusicPlayer: PlaylistMusicPlayer
    private lateinit var layout: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playlistMusicPlayer = PlaylistMusicPlayer.getInstance(context!!)
        initComponents()
    }

    private fun initComponents() {
        layout = fragmentPlaylist
        with(layout) {
            textTitle.text = requireArguments().getString(EXTRA_TITLE)
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
                context,
                RecyclerView.VERTICAL,
                false
            )
            setHasFixedSize(true)
            adapter =
                PlaylistMusicsAdapter(list) { music ->
                    val intent =
                        MusicPlayerActivity.getStartIntent(
                            context,
                            music.title,
                            music.artist,
                            music.albumUriId,
                            getString(EXTRA_PLAYLIST_NAME)
                        )

                    chooseMusic(music.id)
                    context.startActivity(intent)
                }
        }
    }

    override fun chooseMusic(id: UUID) {
        playlistMusicPlayer.chooseMusic(id)
    }

    private fun setMusicList() {
        val viewModel: PlaylistMusicsViewModel = PlaylistMusicsViewModel
            .ViewModelFactory(PlaylistMusicsDataSourceLocal(context!!))
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

        fun getInstance(title: String): Fragment {
            val bundle = Bundle()
            bundle.putString(EXTRA_TITLE, title)

            return LocalSongsFragment()
        }

    }
}
