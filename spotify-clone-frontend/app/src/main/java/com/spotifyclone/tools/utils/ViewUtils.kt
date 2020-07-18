package com.spotifyclone.tools.utils

import android.view.View
import android.view.ViewGroup

class ViewUtils {
    companion object {
        fun setTopMargin(v: View, topMargin: Int) {
            val lp = v.layoutParams as ViewGroup.MarginLayoutParams
            if (lp.topMargin != topMargin) {
                lp.topMargin = topMargin
                v.layoutParams = lp
            }
        }

        fun setPaddingTop(v: View, top: Int) {
            if (v.paddingTop != top) {
                v.setPadding(v.paddingLeft, top, v.paddingRight, v.paddingBottom)
            }
        }

        fun setPaddingBottom(v: View, bottom: Int) {
            if (v.paddingBottom != bottom) {
                v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, bottom)
            }
        }
    }
}