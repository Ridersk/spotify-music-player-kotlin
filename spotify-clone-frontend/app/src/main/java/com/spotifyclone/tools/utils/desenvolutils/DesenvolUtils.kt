package com.spotifyclone.tools.utils.desenvolutils

import android.content.Context
import android.content.Intent
import com.spotifyclone.presentation.playlist.LocalSongsFragment

class DesenvolUtils {

    companion object {
        fun appInDesenvol(): Boolean {
            return false
        }

        fun getActivityBeingTested(context: Context): Intent {
            return Intent(context, LocalSongsFragment::class.java)
        }
    }
}