package com.spotifyclone.presentation.base

import androidx.appcompat.widget.Toolbar

data class ToolbarParameters(
    val toolbar: Toolbar,
    val titleIdRes: Int = 0,
    val option1 : Pair<Int, () -> Unit>? = null,
    val option2: Pair<Int, () -> Unit>? = null,
    val option3: Pair<Int, () -> Unit>? = null
)