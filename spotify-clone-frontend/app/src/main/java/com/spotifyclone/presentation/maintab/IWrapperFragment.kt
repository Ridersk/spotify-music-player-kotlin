package com.spotifyclone.presentation.maintab

import android.os.Bundle
import androidx.fragment.app.Fragment

interface IWrapperFragment {
    fun onReplace(fragment: Fragment, args: Bundle)
    fun onBackPressed(): Boolean
}