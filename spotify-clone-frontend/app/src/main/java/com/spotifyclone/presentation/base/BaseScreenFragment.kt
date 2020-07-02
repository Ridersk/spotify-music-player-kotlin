package com.spotifyclone.presentation.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseScreenFragment(private val parentActivity: BaseActivity) : Fragment() {

    override fun onResume() {
        parentActivity.updateToolbar(getToolbar())
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentActivity.updateToolbar(getToolbar())
        initComponents()
    }

    protected abstract fun initComponents()
    protected abstract fun getToolbar(): ToolbarParameters
}