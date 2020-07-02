package com.spotifyclone.presentation.music

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannedString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.italic
import androidx.fragment.app.Fragment
import com.spotifyclone.R
import com.spotifyclone.presentation.base.BaseFragment
import com.spotifyclone.tools.utils.ImageUtils
import kotlinx.android.synthetic.main.fragment_music_player.*

class MusicPlayerFragment private constructor() : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music_player, container, false)
    }

    override fun initComponents() {
        val label: SpannedString = buildSpannedString {
            append(requireArguments().getString(EXTRA_MUSIC_TITLE))
            color(ContextCompat.getColor(requireContext(), R.color.lightGray)) {
                append(" • ${requireArguments().getString(EXTRA_MUSIC_ARTIST)}")
            }
        }

        txtMusicLabel.text = label

        ImageUtils.insertBitmapInView(
            activity!!.applicationContext,
            imgAlbum,
            requireArguments().getLong(EXTRA_ALBUM_URI_ID, -1)
        )
    }

    companion object {
        private const val EXTRA_MUSIC_TITLE = "EXTRA_MUSIC_TITLE"
        private const val EXTRA_MUSIC_ARTIST = "EXTRA_MUSIC_ARTIST"
        private const val EXTRA_ALBUM_URI_ID = "EXTRA_ALBUM_URI_ID"

        fun getInstance(musicTitle: String, musicArtist: String, albumUrId: Long): Fragment {
            val fragment = MusicPlayerFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_MUSIC_TITLE, musicTitle)
            bundle.putString(EXTRA_MUSIC_ARTIST, musicArtist)
            bundle.putLong(EXTRA_ALBUM_URI_ID, albumUrId)
            fragment.arguments = bundle
            return fragment
        }
    }
}