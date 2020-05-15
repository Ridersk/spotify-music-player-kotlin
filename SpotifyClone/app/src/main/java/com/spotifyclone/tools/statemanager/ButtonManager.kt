package com.spotifyclone.tools.statemanager

import android.content.Context
import android.view.View

class ButtonManager (
    private val context: Context,
    private val component: View,
    private val drawableId: Int,
    private val onClick: (() -> Unit) = {}
) {

    private var drawOptions: List<Int> = listOf(this.drawableId)
    private var selectedOption: Int = 0

    init {
        this.setOnClick(this.onClick)
        this.renderIcon()
    }

    constructor(context: Context, component: View, drawOptions: List<Int>, onClick: (() -> Unit)) :
            this(context, component, drawOptions[0], onClick) {

        this.drawOptions = drawOptions
    }

    private fun renderIcon() {
        component.background = context.getDrawable(drawOptions[selectedOption])
    }

    private fun setOnClick (onClick: () -> Unit = {}) {
        component.setOnClickListener {
            onClick.invoke()
            this.toggleOption()
        }
    }

    private fun toggleOption() {
        selectedOption = (selectedOption + 1) % drawOptions.size
        this.renderIcon()
    }

}