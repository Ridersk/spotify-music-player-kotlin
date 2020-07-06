package com.spotifyclone.data.repository

import android.content.Context
import com.spotifyclone.R
import com.spotifyclone.data.MusicsResult
import com.spotifyclone.data.model.Music
import com.spotifyclone.tools.filemanager.MusicFileManagerApp

class PlaylistMusicsDataSourceLocal(private val context: Context) : PlaylistMusicsRepository {
    override fun getMusics(resultCallback: (result: MusicsResult) -> Unit) {
        val musics = getMusicsFromStorage()

        if (!musics.isNullOrEmpty()) {
            resultCallback.invoke(MusicsResult.Success(musics))
        } else {
            resultCallback.invoke(
                MusicsResult.ApiError(
                    ERROR_NOT_FOUND,
                    R.string.fragment_local_songs_text_empty_list,
                    R.string.fragment_local_songs_text_empty_list_description
                )
            )
        }
    }

    private fun getMusicsFromStorage(): MutableList<Music> {
        return MusicFileManagerApp.getMusicList(context)
    }

    companion object {
        private const val ERROR_NOT_FOUND = 404
    }
}