package com.vasu.image.video.pickrandom.galleryapp.util

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView

object ImageFlipper {
    fun flip(imageView: ImageView, flipDirection: FlipDirection?) {
        val drawable = flip(imageView.drawable, flipDirection)
        imageView.setImageDrawable(drawable)
    }

    fun flip(drawable: Drawable?, flipDirection: FlipDirection?): Drawable? {
        if (drawable == null) {
            return null
        }
        val `in` = (drawable as BitmapDrawable).bitmap
        return BitmapDrawable(Resources.getSystem(), flip(`in`, flipDirection))
    }

    fun flip(input: Bitmap, flipDirection: FlipDirection?): Bitmap? {
        var output: Bitmap? = null
        when (flipDirection) {
            FlipDirection.VERTICAL -> output = flipVertical(input)
            FlipDirection.HORIZONTAL -> output = flipHorizontal(input)
            else -> {

            }
        }
        return output
    }

    private fun flipVertical(input: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postScale(1f, -1f, (input.width / 2).toFloat(), (input.height / 2).toFloat())
        return Bitmap.createBitmap(
            input,
            0,
            0,
            input.width,
            input.height,
            matrix,
            false
        )
    }

    private fun flipHorizontal(input: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postScale(-1f, 1f, (input.width / 2).toFloat(), (input.height / 2).toFloat())
        return Bitmap.createBitmap(
            input,
            0,
            0,
            input.width,
            input.height,
            matrix,
            false
        )
    }
}