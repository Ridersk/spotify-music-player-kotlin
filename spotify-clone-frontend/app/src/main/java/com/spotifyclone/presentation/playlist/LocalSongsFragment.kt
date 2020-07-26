package com.spotifyclone.presentation.playlist

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.spotifyclone.R
import com.spotifyclone.components.scroll.CustomBehaviorNestedScroll
import com.spotifyclone.presentation.dialogs.CustomDialog
import com.spotifyclone.data.model.Music
import com.spotifyclone.data.repository.PlaylistMusicsDataSourceLocal
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.BaseScreenFragment
import com.spotifyclone.presentation.base.ToolbarParameters
import com.spotifyclone.presentation.notifications.MusicPlayerNotification
import com.spotifyclone.tools.animations.ReducerAndRegain
import com.spotifyclone.tools.musicplayer.*
import com.spotifyclone.tools.permissions.AppPermissions
import com.spotifyclone.tools.utils.MathUtils
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.util.*
import kotlin.math.abs

class LocalSongsFragment private constructor(private val parentActivity: BaseActivity) :
    BaseScreenFragment(parentActivity), PlaylistInterface,
    MusicProvider, MusicObserver, ViewTreeObserver.OnScrollChangedListener {

    private val playlistMusicPlayer = PlaylistMusicPlayer.getInstance(parentActivity)
    private lateinit var viewModel: PlaylistMusicsViewModel
    private lateinit var requiredPermissionDialog: CustomDialog
    private val musicList: MutableList<Music> = mutableListOf()
    private lateinit var playlistAdapter: PlaylistMusicsAdapter
    private val requiredPermissions = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    init {
        playlistMusicPlayer.addMusicObserver(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createRecyclerList()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initComponents() {
        txtTitle.text = requireArguments().getString(EXTRA_PLAYLIST_NAME)
        btnFloat.text = getString(R.string.fragment_local_songs_button_random_play)
        btnFloat.setOnTouchListener { view, event ->
            ReducerAndRegain(parentActivity).onTouch(view, event)
        }

        nestedscrollview.viewTreeObserver.addOnScrollChangedListener(this)
        setMusicList()
    }

    private fun createRecyclerList() {
        this.playlistAdapter =
            PlaylistMusicsAdapter(parentActivity, this.musicList, nestedscrollview) { music ->
                chooseMusic(music.id)
            }

        with(recyclerList) {
            val lm = androidx.recyclerview.widget.LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            )
            layoutManager = lm
            setHasFixedSize(true)
            adapter = playlistAdapter
        }
    }


    override fun onScrollChanged() {
        val coordinatesCardView = intArrayOf(0, 0)
        btnFloat?.getLocationOnScreen(coordinatesCardView)
        val btnPositionY = coordinatesCardView[1]
        val toolbarBottomPos: Int = toolbarMain?.bottom ?: 0
        val limitPos = toolbarBottomPos + CustomBehaviorNestedScroll.MARGIN_TO_LIMIT
        val scaleProportion = MathUtils.calculateProportion(
            abs(btnPositionY - limitPos),
            DIST_CARD_VIEW_TOOLBAR,
            0.2F
        )
        val alpha = MathUtils.calculateReverseProportion(
            abs(btnPositionY - limitPos),
            DIST_CARD_VIEW_TOOLBAR,
            1.0F
        )

        txtTitle?.scaleX = scaleProportion
        txtTitle?.scaleY = scaleProportion
        textToolbarTitle?.scaleX = alpha
        parentActivity.updateTitleAlpha(alpha)
    }

    override fun changedMusic(music: Music) {
        onResume()
    }

    override fun onResume() {
        val selectedMusic = playlistMusicPlayer.getCurrentMusic()?.id
        if (this::playlistAdapter.isInitialized) {
            selectedMusic?.let { this.playlistAdapter.select(selectedMusic) }
        }
        super.onResume()
    }

    override fun getPlaylistName() {}

    override fun updatedList(newMusicList: List<Music>) {
        this.musicList.clear()
        this.musicList.addAll(newMusicList)
    }

    override fun chooseMusic(id: UUID) {
        playlistMusicPlayer.chooseMusic(id)
        val musicPlayerNotification = MusicPlayerNotification.getInstance(parentActivity)
        musicPlayerNotification.createNotification()
        onResume()
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
            title = requireArguments().getString(EXTRA_PLAYLIST_NAME),
            option1 = Pair(R.drawable.ic_back, { parentActivity.onBackPressed() }),
            option3 = Pair(R.drawable.ic_options, {})
        )

    companion object {
        private const val EXTRA_PLAYLIST_NAME = "EXTRA_PLAYLIST_NAME"
        private const val DIST_CARD_VIEW_TOOLBAR = 275F

        fun getInstance(parent: BaseActivity, playlist: String): LocalSongsFragment {
            val fragment = LocalSongsFragment(parent)
            val bundle = Bundle()
            bundle.putString(EXTRA_PLAYLIST_NAME, playlist)
            fragment.arguments = bundle
            return fragment
        }

    }
}
