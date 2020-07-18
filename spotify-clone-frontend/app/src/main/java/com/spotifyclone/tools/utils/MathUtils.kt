package com.spotifyclone.tools.utils

import kotlin.math.abs

class MathUtils {

    companion object {
        fun calculateProportion(piece: Number, total: Number, weight: Float): Float {
            return ((piece.toFloat() / total.toFloat()) * weight) + 1.0F - weight
        }

        fun calculateReverseProportion(piece: Number, total: Number, weight: Float): Float {
            return ((abs(piece.toFloat() - total.toFloat()) /
                    total.toFloat() * weight) +
                    1.0F - weight)
        }
    }
}
