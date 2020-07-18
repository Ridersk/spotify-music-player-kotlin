package com.spotifyclone.tools.utils

import android.graphics.drawable.GradientDrawable

class ColorUtils {

    companion object {
        fun getGradient(color1: Int, color2: Int): GradientDrawable {
            val gradient = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(color1, color2)
            )

            gradient.cornerRadius = 0f
            gradient.alpha = 100
            return gradient
        }
    }
}