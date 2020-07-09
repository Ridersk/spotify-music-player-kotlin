package com.spotifyclone.presentation.main

import androidx.fragment.app.Fragment

interface IWrapperFragment {
    fun onReplace(fragment: Fragment)
    fun onBackPressed(): Boolean
}