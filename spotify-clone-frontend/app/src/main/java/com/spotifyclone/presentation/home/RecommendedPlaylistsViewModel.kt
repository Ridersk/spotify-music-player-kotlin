package com.spotifyclone.presentation.home

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.spotifyclone.R
import com.spotifyclone.data.model.Playlist
import java.lang.IllegalArgumentException

class RecommendedPlaylistsViewModel(val context: Context) : ViewModel() {

    val recommendedPlaylistsLiveData: MutableLiveData<List<Playlist>> = MutableLiveData()

    fun getRecommendedPlaylists() {
        val recommendedList = listOf(
            Playlist("Local Songs", context.getString(R.string.fragment_local_songs_title))
        )

        recommendedPlaylistsLiveData.value = recommendedList
    }

    class ViewModelFactory(
        val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecommendedPlaylistsViewModel::class.java)) {
                return RecommendedPlaylistsViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
