package com.spotifyclone.presentation.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    override fun onDestroy() {
        removeComponents()
        super.onDestroy()
    }

    protected abstract fun initComponents()
    protected open fun removeComponents() {}
}