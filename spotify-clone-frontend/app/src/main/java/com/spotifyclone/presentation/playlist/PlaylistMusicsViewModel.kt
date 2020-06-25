package com.spotifyclone.presentation.playlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.spotifyclone.data.MusicsResult
import com.spotifyclone.data.model.Music
import com.spotifyclone.data.repository.PlaylistMusicsRepository
import java.lang.IllegalArgumentException

open class PlaylistMusicsViewModel(
    private val dataSource: PlaylistMusicsRepository
) : ViewModel() {

    val musicsLiveData: MutableLiveData<List<Music>> = MutableLiveData()
    val viewFlipperLiveData: MutableLiveData<ViewFlipperPlayslistMusics> = MutableLiveData()

    fun getMusics() {
        dataSource.getMusics { result: MusicsResult ->
            when (result) {
                is MusicsResult.Success -> {
                    musicsLiveData.value = result.musics
                    viewFlipperLiveData.value = ViewFlipperPlayslistMusics(VIEW_FLIPPER_MUSICS)
                }
                is MusicsResult.ApiError -> {
                    viewFlipperLiveData.value = ViewFlipperPlayslistMusics(
                        VIEW_FLIPPER_ERROR,
                        result.warningResId,
                        result.descriptionResId
                    )
                }
                is MusicsResult.ServerError -> {
                }
            }
        }
    }

    class ViewModelFactory(
        private val dataSource: PlaylistMusicsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlaylistMusicsViewModel::class.java)) {
                return PlaylistMusicsViewModel(dataSource) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    companion object {
        private const val VIEW_FLIPPER_MUSICS = 1
        private const val VIEW_FLIPPER_ERROR = 2
    }
}