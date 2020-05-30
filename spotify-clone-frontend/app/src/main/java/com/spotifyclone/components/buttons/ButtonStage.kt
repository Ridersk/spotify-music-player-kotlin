package com.spotifyclone.components.buttons

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableContainer.DrawableContainerState
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
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
    private lateinit var statusDrawableOptions: Array<Drawable>

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

//        if (mainImage is StateListDrawable) {
//            val t = 0
//        }

        mainDrawableOptions = getDrawableChildren(mainImage)
        val firstItem: Drawable = mainDrawableOptions[0]

        mMainImageButton.background = firstItem

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
}