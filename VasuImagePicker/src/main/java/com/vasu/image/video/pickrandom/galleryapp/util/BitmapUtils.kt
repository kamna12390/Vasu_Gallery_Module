package com.vasu.image.video.pickrandom.galleryapp.util

import android.graphics.*


object BitmapUtils {
    fun getCroppedBitmap(src: Bitmap, path: Path?): Bitmap {
        val output = Bitmap.createBitmap(
            src.width,
            src.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = -0x1000000
        canvas.drawPath(path!!, paint)

        // Keeps the source pixels that cover the destination pixels,
        // discards the remaining source and destination pixels.
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(src, 0f, 0f, paint)
        return output
    }
}