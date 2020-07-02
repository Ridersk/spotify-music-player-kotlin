package com.spotifyclone.presentation.base

import android.os.Bundle
import android.view.View

abstract class BaseScreenFragment(private val parentActivity: BaseActivity) : BaseFragment() {

    override fun onResume() {
        parentActivity.updateToolbar(getToolbar())
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentActivity.updateToolbar(getToolbar())
    }

    protected abstract fun getToolbar(): ToolbarParameters
}