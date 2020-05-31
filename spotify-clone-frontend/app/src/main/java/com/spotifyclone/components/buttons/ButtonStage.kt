package com.spotifyclone.components.buttons

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableContainer.DrawableContainerState
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.RemoteViews
import com.spotifyclone.R

@RemoteViews.RemoteView
class ButtonStage(parentContext: Context, attrs: AttributeSet?) :
    RelativeLayout(parentContext, attrs) {
    private lateinit var mMainImageButton: ImageButton
    private lateinit var mStatusImageView: ImageView
    private lateinit var mainDrawableOptions: Array<Drawable>
    private var statusFun: () -> Boolean = { false }
    private var mainButtonStatesFun: () -> Int = { 0 }

    init {
        val inflater: LayoutInflater =
            parentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val typedArray: TypedArray =
            parentContext.obtainStyledAttributes(attrs, R.styleable.ButtonStage, 0, 0)
        inflater.inflate(R.layout.custom_button_stage, this, true)
        setMainImage(typedArray)
        setStatusImage(typedArray)
        typedArray.recycle()
    }

    constructor(context: Context) : this(context, null)

    private fun setMainImage(typedArray: TypedArray) {
        mMainImageButton = getChildAt(0) as ImageButton
        val mainImage: StateListDrawable =
            typedArray.getDrawable(R.styleable.ButtonStage_mainImage) as StateListDrawable

        mainDrawableOptions = getDrawableChildren(mainImage)

        mMainImageButton.background = mainDrawableOptions[0]

    }

    private fun setStatusImage(typedArray: TypedArray) {
        mStatusImageView = getChildAt(1) as ImageView
        val statusImage: Drawable? =
            typedArray.getDrawable(R.styleable.ButtonStage_statusImage)

        mStatusImageView.background = statusImage
    }

    private fun getDrawableChildren(stateListDrawable: StateListDrawable): Array<Drawable> {
        val drawableContainerState: DrawableContainerState =
            stateListDrawable.constantState as DrawableContainerState

        return drawableContainerState.children
    }

    fun setMainButtonStatesProvider(statusFun: () -> Int) {
        this.mainButtonStatesFun = statusFun

        mMainImageButton.background = updateState()
    }

    fun setStatusProvider(statusFun: () -> Boolean) {
        this.statusFun = statusFun

        mStatusImageView.visibility = updateVisibility()
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        mMainImageButton.setOnClickListener { v ->
            listener?.onClick(v)
            mStatusImageView.visibility = updateVisibility()
            mMainImageButton.background = updateState()
        }
    }

    private fun updateVisibility() = if (statusFun.invoke()) View.VISIBLE else View.INVISIBLE

    private fun updateState() = mainDrawableOptions[this.mainButtonStatesFun.invoke()]
}