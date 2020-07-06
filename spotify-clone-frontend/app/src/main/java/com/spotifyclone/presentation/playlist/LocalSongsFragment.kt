package com.spotifyclone.presentation.playlist

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.spotifyclone.R
import com.spotifyclone.components.dialogs.CustomDialog
import com.spotifyclone.data.model.Music
import com.spotifyclone.data.repository.PlaylistMusicsDataSourceLocal
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.BaseScreenFragment
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.presentation.music.MusicPlayerActivity
import com.spotifyclone.tools.musicplayer.PlaylistMusicPlayer
import com.spotifyclone.tools.musicplayer.PlaylistObserverProvider
import com.spotifyclone.tools.musicplayer.PlaylistObserver
import com.spotifyclone.tools.permissions.AppPermissions
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.android.synthetic.main.fragment_playlist.view.*
import java.util.*

class LocalSongsFragment private constructor(private val parentActivity: BaseActivity) :
    BaseScreenFragment(parentActivity), PlaylistInterface,
    PlaylistObserver<Music> {

    private lateinit var playlistMusicPlayer: PlaylistMusicPlayer
    private lateinit var layout: ViewGroup
    private lateinit var viewModel: PlaylistMusicsViewModel
    private lateinit var requiredPermissionDialog: CustomDialog
    private val musicList: MutableList<Music> = mutableListOf()
    private lateinit var playlistAdapter: PlaylistMusicsAdapter
    private val requiredPermissions = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun initComponents() {
        playlistMusicPlayer = PlaylistMusicPlayer.getInstance(context!!)
        layout = fragmentPlaylist
        textTitle.text = requireArguments().getString(EXTRA_TITLE)
        buttonRandomPlay.text = getString(R.string.fragment_local_songs_button_random_play)
        layoutMusicControl.visibility = View.GONE
        setMusicList()
    }

    override fun onResume() {
        val selected =  this.musicList.indexOfFirst { music ->  music.id == playlistMusicPlayer.getCurrentMusic()?.id }
        this.playlistAdapter.select(selected)
        this.playlistAdapter.notifyDataSetChanged()
        super.onResume()
    }

    override fun getPlaylistName() {}

    override fun receiverList(list: List<Music>) {
        this.musicList.addAll(list)
        this.playlistAdapter = PlaylistMusicsAdapter(parentActivity, this.musicList) { music ->
            val intent =
                MusicPlayerActivity.getIntent(
                    parentActivity,
                    music.title,
                    music.artist,
                    music.albumUriId?:-1,
                    getString(EXTRA_PLAYLIST_NAME)
                )

            chooseMusic(music.id)
            parentActivity.startActivity(intent)
        }

        with(layout.recyclerMusics) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            )
            setHasFixedSize(true)
            adapter = playlistAdapter
        }
    }

    override fun chooseMusic(id: UUID) {
        playlistMusicPlayer.chooseMusic(id)
    }

    private fun setMusicList() {
        this.viewModel = PlaylistMusicsViewModel
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

        this.addDialog(
            getString(R.string.dialog_alert_permissions_title),
            getString(R.string.dialog_alert_permissions_description)
        )

        this.requestPermissions(this.requiredPermissions) { viewModel.getMusics() }
    }

    private fun requestPermissions(requiredPermissions: List<String>, callback: () -> Unit) {
        AppPermissions.checkMultiplePermissions(
            parentActivity,
            requiredPermissions,
            callback,
            { requiredPermissionDialog.show() }
        )
    }

    private fun addDialog(title: String, description: String) {
        this.requiredPermissionDialog = CustomDialog.Builder(parentActivity)
            .title(title)
            .description(description)
            .mainButton(getString(R.string.dialog_alert_btn_permissions_allow_storage_access)) {
                this.requestPermissions(this.requiredPermissions) { viewModel.getMusics() }

            }
            .optionalButton(getString(R.string.dialog_alert_btn_permissions_cancel)) {}
            .build()
    }

    override fun getToolbar(): ToolbarParameters =
        ToolbarParameters(
            option1 = Pair(R.drawable.ic_back, { parentActivity.onBackPressed() }),
            option3 = Pair(R.drawable.ic_options, {})
        )

    companion object {
        private const val EXTRA_PLAYLIST_NAME: Int = R.string.fragment_local_songs_title
        private const val EXTRA_TITLE = "EXTRA_TITLE"

        fun getInstance(parent: BaseActivity, title: String): Fragment {
            val bundle = Bundle()
            bundle.putString(EXTRA_TITLE, title)
            return LocalSongsFragment(parent)
        }

    }
}
