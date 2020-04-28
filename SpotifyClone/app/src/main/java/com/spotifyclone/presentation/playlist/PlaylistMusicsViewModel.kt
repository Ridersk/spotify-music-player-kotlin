package com.spotifyclone.presentation.playlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.spotifyclone.data.model.Music
import java.lang.IllegalArgumentException

class PlaylistMusicsViewModel: ViewModel() {

    val musicsLiveData: MutableLiveData<List<Music>> = MutableLiveData()

    fun getMusics() {
        var musicList = mutableListOf(
            Music("Session", "Linkin Park", "Meteora"),
            Music("Paradise City", "Guns N'Roses", "Use Your Illusion II"),
            Music("Critical Acclaim", "Avenged Sevenfold", "")
        )

        for (i in 0..26) {
            musicList.add(musicList[i % 3])
        }

        musicsLiveData.value = musicList
    }

    class ViewModelFactory : ViewModelProvider.Factory {
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlaylistMusicsViewModel::class.java)) {
                return PlaylistMusicsViewModel() as T
            }

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}