package com.spotifyclone.tools.animations

import android.animation.AnimatorInflater
import android.content.Context
import android.view.MotionEvent
import android.view.View
import com.spotifyclone.R

class ReducerAndRegain(private val context: Context): View.OnTouchListener {
    override fun onTouch(view: View?, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val reducer = AnimatorInflater.loadAnimator(context, R.animator.reduce_size)
                reducer.setTarget(view)
                reducer.start()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_MOVE -> {
                val regainer = AnimatorInflater.loadAnimator(context, R.animator.regain_size)
                regainer.setTarget(view)
                regainer.start()
            }
        }
        return false
    }
}
