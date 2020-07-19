package com.spotifyclone.presentation.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spotifyclone.R
import com.spotifyclone.presentation.base.BaseActivity
import com.spotifyclone.presentation.base.BaseScreenFragment
import com.spotifyclone.presentation.base.ToolbarParameters

class LibraryFragment private constructor(parentActivity: BaseActivity) :
    BaseScreenFragment(parentActivity) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_page_library, container, false)
    }

    override fun getToolbar(): ToolbarParameters = ToolbarParameters()

    override fun initComponents() {
    }

    companion object {
        fun getInstance(parent: BaseActivity): LibraryFragment {
            val homeFragment = LibraryFragment(parent)
            val bundle = Bundle()
            homeFragment.arguments = bundle
            return homeFragment
        }
    }
}