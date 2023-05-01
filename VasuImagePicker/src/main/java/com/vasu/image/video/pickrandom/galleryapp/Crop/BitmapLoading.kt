package com.example.imagecrop.Crop

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import java.lang.Exception
import java.lang.ref.WeakReference

class BitmapLoading(
    cropImageView: CropImageView,
    val uri: Uri
) : AsyncTask<Void?, Void?, BitmapLoading.Result?>() {
    private val mCropImageViewReference: WeakReference<CropImageView>
    private val mContext: Context
    private val mWidth: Int
    private val mHeight: Int

    // endregion
    init {
        mCropImageViewReference = WeakReference(cropImageView)
        mContext = cropImageView.context
        val metrics = cropImageView.resources.displayMetrics
        val densityAdj = if (metrics.density > 1) (1 / metrics.density).toDouble() else 1.toDouble()
        mWidth = (metrics.widthPixels * densityAdj).toInt()
        mHeight = (metrics.heightPixels * densityAdj).toInt()
    }


    protected override fun doInBackground(vararg p0: Void?): Result? {
        return try {
            if (!isCancelled) {
                val decodeResult = BitmapUtils.decodeSampledBitmap(mContext, uri, mWidth, mHeight)
                if (!isCancelled) {
                    val rotateResult =
                        BitmapUtils.rotateBitmapByExif(decodeResult.bitmap, mContext, uri)
                    return Result(
                        uri, rotateResult.bitmap, decodeResult.sampleSize, rotateResult.degrees
                    )
                }
            }
            null
        } catch (e: Exception) {
            Result(uri, e)
        }
    }

    override fun onPostExecute(result: Result?) {
        if (result != null) {
            var completeCalled = false
            if (!isCancelled) {
                val cropImageView = mCropImageViewReference.get()
                if (cropImageView != null) {
                    completeCalled = true
                    cropImageView.onSetImageUriAsyncComplete(result)
                }
            }
            if (!completeCalled && result.bitmap != null) {
                // fast release of unused bitmap
                result.bitmap.recycle()
            }
        }
    }
    /** The result of BitmapLoadingWorkerTask async loading.  */
    class Result {
        /** The Android URI of the image to load  */
        val uri: Uri

        /** The loaded bitmap  */
        val bitmap: Bitmap?

        /** The sample size used to load the given bitmap  */
        val loadSampleSize: Int

        /** The degrees the image was rotated  */
        val degreesRotated: Int

        /** The error that occurred during async bitmap loading.  */
        val error: Exception?

        internal constructor(uri: Uri, bitmap: Bitmap?, loadSampleSize: Int, degreesRotated: Int) {
            this.uri = uri
            this.bitmap = bitmap
            this.loadSampleSize = loadSampleSize
            this.degreesRotated = degreesRotated
            error = null
        }

        internal constructor(uri: Uri, error: Exception?) {
            this.uri = uri
            bitmap = null
            loadSampleSize = 0
            degreesRotated = 0
            this.error = error
        }
    } // endregion
}