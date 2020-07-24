package com.spotifyclone.presentation.music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spotifyclone.R
import com.spotifyclone.presentation.base.BaseFragment
import com.spotifyclone.tools.utils.ImageUtils
import kotlinx.android.synthetic.main.item_fragment_album_art.*

class ItemAlbumArtFragment private constructor() : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.item_fragment_album_art, container, false)

    }

    override fun initComponents() {
        activity?.let { activity ->
            albumArt.setImageBitmap(
                ImageUtils.getBitmapAlbumArt(
                    activity.applicationContext,
                    requireArguments().getLong(EXTRA_ALBUM_URI_ID, -1)
                )
            )
        }
    }

    companion object {
        private const val EXTRA_ALBUM_URI_ID = "EXTRA_ALBUM_URI_ID"

        fun getInstance(
            albumUriId: Long?
        ): ItemAlbumArtFragment {
            val fragment = ItemAlbumArtFragment()
            val bundle = Bundle()
            bundle.putLong(EXTRA_ALBUM_URI_ID, albumUriId ?: -1L)
            fragment.arguments = bundle
            return fragment
        }
    }
}