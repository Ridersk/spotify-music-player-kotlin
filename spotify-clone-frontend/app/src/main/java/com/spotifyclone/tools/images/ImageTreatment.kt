package com.spotifyclone.tools.images

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.TypedValue

class ImageTreatment {

    companion object {
        fun mergeIcons(mainIcon: Drawable?, supportIcon: Drawable?): Drawable {
            val horizontalInset = (mainIcon!!.intrinsicWidth - supportIcon!!.intrinsicWidth)

            val finalDrawable = LayerDrawable(arrayOf(mainIcon, supportIcon))

            finalDrawable.setLayerInset(0, 0, 0, 0, supportIcon.intrinsicHeight)
            finalDrawable.setLayerInset(
                1,
                horizontalInset,
                mainIcon.intrinsicHeight,
                horizontalInset,
                0
            )
            return finalDrawable
        }

        fun resizeDrawable(context:Context, image: Drawable?, width: Int, height: Int): BitmapDrawable {
            return BitmapDrawable(
                context.resources,
                Bitmap.createScaledBitmap(
                    getBitmap(image),
                    dipToPixels(context, width),
                    dipToPixels(context, height),
                    false
                )
            )
        }

        private fun getBitmap(drawable: Drawable?): Bitmap {
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable!!.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        private fun dipToPixels(context: Context, dipValue: Int): Int {
            val metrics = context.resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue.toFloat(), metrics)
                .toInt()
        }
    }
}