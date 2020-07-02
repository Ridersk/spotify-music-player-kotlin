package com.spotifyclone.presentation.base

data class ToolbarParameters(
    val title: String? = "",
    val subTitle: String? = "",
    val option1 : Pair<Int, () -> Unit>? = null,
    val option2: Pair<Int, () -> Unit>? = null,
    val option3: Pair<Int, () -> Unit>? = null
)