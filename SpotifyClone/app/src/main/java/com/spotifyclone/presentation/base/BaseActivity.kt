package com.spotifyclone.presentation.base

import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.include_toolbar.view.*


abstract class BaseActivity : AppCompatActivity() {

    private var toolbarArgs: ToolbarParameters? = null

    protected fun setupToolbar(toolbarArgs: ToolbarParameters) {
        this.toolbarArgs = toolbarArgs

        val toolbar: Toolbar = toolbarArgs.toolbar

        toolbar.let {
            with(toolbar) {
                toolbarTextTitle.text = getString(toolbarArgs.titleIdRes)

                if (toolbarArgs.option1Idres > 0) {
                    iconOption1.setImageDrawable(getDrawable(toolbarArgs.option1Idres))
                }
                if (toolbarArgs.option2IdRes > 0) {
                    iconOption2.setImageDrawable(getDrawable(toolbarArgs.option2IdRes))
                }
                if (toolbarArgs.option3Idres > 0) {
                    iconOption3.setImageDrawable(getDrawable(toolbarArgs.option3Idres))
                }
            }
            setSupportActionBar(toolbarMain)
        }
    }
}