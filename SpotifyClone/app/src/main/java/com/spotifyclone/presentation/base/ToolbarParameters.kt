package com.spotifyclone.presentation.base

import androidx.appcompat.widget.Toolbar

data class ToolbarParameters(
    val toolbar: Toolbar,
    val titleIdRes: Int = 0,
    val option1Idres: Int = 0,
    val option2IdRes: Int = 0,
    val option3Idres: Int = 0
)