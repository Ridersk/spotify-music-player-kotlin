package com.spotifyclone.presentation.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.spotifyclone.data.model.Playlist
import java.lang.IllegalArgumentException

class RecommendedPlaylistsViewModel : ViewModel() {

    val recommendedPlaylistsLiveData: MutableLiveData<List<Playlist>> = MutableLiveData()

    fun getRecommendedPlaylists() {
        val recommendedList = listOf(
            Playlist("Local Songs"),
            Playlist("Synthwave"),
            Playlist("Wake radio"),
            Playlist("Holographic"),
            Playlist("Strange Encounters Radio Station B landscape"),
            Playlist("Betters")
        )

        recommendedPlaylistsLiveData.value = recommendedList
    }

    class ViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecommendedPlaylistsViewModel::class.java)) {
                return RecommendedPlaylistsViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}