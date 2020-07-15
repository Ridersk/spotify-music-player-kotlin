package com.spotifyclone.presentation.main

import androidx.fragment.app.Fragment

interface IWrapperFragment {
    fun reset()
    fun onReplace(fragment: Fragment)
    fun onBackPressed(): Boolean
}