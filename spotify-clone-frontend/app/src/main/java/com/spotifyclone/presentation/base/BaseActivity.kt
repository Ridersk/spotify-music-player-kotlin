package com.spotifyclone.presentation.base

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.transition.Transition
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.spotifyclone.R
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.include_toolbar.view.*


abstract class BaseActivity : AppCompatActivity() {

    private lateinit var enterTransition: Transition
    private lateinit var exitTransition: Transition

    override fun createConfigurationContext(overrideConfiguration: Configuration): Context {
        window.requestFeature(android.view.Window.FEATURE_ACTIVITY_TRANSITIONS)
        return super.createConfigurationContext(overrideConfiguration)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        if (this::enterTransition.isInitialized) {
            window.enterTransition = enterTransition
        }
        initComponents()
    }

    override fun onPause() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        super.onPause()
    }

    protected fun setupToolbar(toolbarArgs: ToolbarParameters) {
        setSupportActionBar(buildToolbar(toolbarArgs))
    }

    fun updateToolbar(toolbarArgs: ToolbarParameters) {
        buildToolbar(toolbarArgs)
    }

    private fun buildToolbar(toolbarArgs: ToolbarParameters): Toolbar {
        val toolbar: Toolbar = toolbarMain

        with(toolbar) {

            if (toolbarArgs.title != null && toolbarArgs.title.isNotEmpty()) {
                textToolbarTitle.text = toolbarArgs.title
                textToolbarTitle.visibility = View.VISIBLE

            } else {
                textToolbarTitle.visibility = View.GONE
            }

            if (toolbarArgs.subTitle != null && toolbarArgs.subTitle.isNotEmpty()) {
                textToolbarSubtitle.text = toolbarArgs.subTitle
                textToolbarSubtitle.visibility = View.VISIBLE
            } else {
                textToolbarSubtitle.visibility = View.GONE
            }

            if (toolbarArgs.option1 != null && toolbarArgs.option1.first > 0) {
                iconOption1.setImageDrawable(getDrawable(toolbarArgs.option1.first))
                iconOption1.setOnClickListener {
                    toolbarArgs.option1.second.invoke()
                }
                iconOption1.visibility = View.VISIBLE
            } else {
                iconOption1.setImageDrawable(null)
                iconOption1.setOnClickListener {}
                iconOption1.visibility = View.GONE
            }


            if (toolbarArgs.option2 != null && toolbarArgs.option2.first > 0) {
                iconOption2.setImageDrawable(getDrawable(toolbarArgs.option2.first))
                iconOption2.setOnClickListener {
                    toolbarArgs.option2.second.invoke()
                }
                iconOption2.visibility = View.VISIBLE
            } else {
                iconOption2.setImageDrawable(null)
                iconOption2.setOnClickListener {}
                iconOption2.visibility = View.GONE
            }

            if (toolbarArgs.option3 != null && toolbarArgs.option3.first > 0) {
                iconOption3.setImageDrawable(getDrawable(toolbarArgs.option3.first))
                iconOption3.setOnClickListener {
                    toolbarArgs.option3.second.invoke()
                }
                iconOption3.visibility = View.VISIBLE
            } else {
                iconOption3.setImageDrawable(null)
                iconOption3.setOnClickListener {}
                iconOption3.visibility = View.GONE
            }
        }
        return toolbar
    }

    override fun onBackPressed() {
        this.removeComponents()
        if (this::exitTransition.isInitialized) {
            window.exitTransition = exitTransition
        }
        super.onBackPressed()
    }

    protected fun setTransitions(enter: Transition, exit: Transition) {
        this.enterTransition = enter
        this.exitTransition =  exit

    }
    protected open fun initComponents() {}
    protected open fun removeComponents() {}

    fun updateTitleAlpha (alpha: Float) {
        textToolbarTitle.alpha = alpha
        textToolbarSubtitle.alpha = alpha
    }
}
