package com.spotifyclone.presentation.playlist

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.spotifyclone.data.model.Music
import com.spotifyclone.tools.filemanager.FileManagerApp
import java.lang.IllegalArgumentException

class PlaylistMusicsViewModel(
    private val parentContext: Context
) : ViewModel() {

    val musicsLiveData: MutableLiveData<List<Music>> = MutableLiveData()

    fun getMusics() {
        var musicList = getRepositoryMusics()

        for (i in 0..26) {
            musicList.add(musicList[i % musicList.size])
        }

        val a = "teste"
        a.length
        musicsLiveData.value = musicList

    }

    private fun getRepositoryMusics(): MutableList<Music> {

        return FileManagerApp.getMusicList(parentContext)
//        return mutableListOf(
//            Music("Session", "Linkin Park", "Meteora"),
//            Music("Paradise City", "Guns N'Roses", "Use Your Illusion II"),
//            Music("Critical Acclaim", "Avenged Sevenfold", "")
//        )
    }

    class ViewModelFactory(private val parentContext: Context) : ViewModelProvider.Factory {
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlaylistMusicsViewModel::class.java)) {
                return PlaylistMusicsViewModel(parentContext) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}