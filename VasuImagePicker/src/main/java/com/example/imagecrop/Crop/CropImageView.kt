package com.example.imagecrop.Crop

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import com.example.imagecrop.Crop.*
import com.example.imagecrop.Crop.BitmapUtils
import com.vasu.image.video.pickrandom.galleryapp.Crop.ImageAnimation
import com.vasu.image.video.pickrandom.galleryapp.Crop.ImageOptions
import com.vasu.image.video.pickrandom.galleryapp.R
import com.vasu.image.video.pickrandom.galleryapp.util.CropImage
import com.vasu.image.video.pickrandom.galleryapp.util.FlipDirection
import com.vasu.image.video.pickrandom.galleryapp.util.ImageFlipper
import kotlinx.coroutines.*
import java.lang.Runnable
import java.lang.ref.WeakReference
import java.util.*
import kotlin.math.log


class CropImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {
    public val mImageView: ImageView
    public val mCropOverlayView: CropOverlayView?
    private val mImageMatrix = Matrix()
    private val mImageInverseMatrix = Matrix()
    public val mProgressBar: ProgressBar
    private val mImagePoints = FloatArray(8)
    private val mScaleImagePoints = FloatArray(8)
    private var mAnimation: ImageAnimation? = null
    private var mBitmap: Bitmap? = null
    private var mInitialDegreesRotated = 0
    private var mDegreesRotated = 0
    private var mFlipHorizontally: Boolean
    private var mFlipVertically: Boolean
    private var mLayoutWidth = 0
    private var mLayoutHeight = 0
    private var mImageResource = 0
    private var mScaleType: ScaleType
    var isSaveBitmapToInstanceState = false
    private var mShowCropOverlay = true
    private var mShowProgressBar = true
    private var mAutoZoomEnabled = true
    val bundle = Bundle()

    private var mMaxZoom: Int
    private var mOnCropOverlayReleasedListener: OnSetCropOverlayReleasedListener? = null
    private var mOnSetCropOverlayMovedListener: OnSetCropOverlayMovedListener? = null
    private var mOnSetCropWindowChangeListener: OnSetCropWindowChangeListener? = null
    private var mOnSetImageUriCompleteListener: OnSetImageUriCompleteListener? = null
    private var mOnCropImageCompleteListener: OnCropImageCompleteListener? = null
    var imageUri: Uri? = null
        private set

    private var mLoadedSampleSize = 1
    private var mZoom = 1f
    private var mZoomOffsetX = 0f
    private var mZoomOffsetY = 0f
    private var mRestoreCropWindowRect: RectF? = null
    private var mRestoreDegreesRotated = 0
    private var mSizeChanged = false
    private var mSaveInstanceStateBitmapUri: Uri? = null
    private var mBitmapLoading: WeakReference<BitmapLoading>? = null
    private var mBitmapCropping: WeakReference<BitmapCropping>? = null
    val mHandler = Handler();


    // endregion
    init {
        var options: ImageOptions? = null
        val intent = if (context is Activity) context.intent else null
        if (intent != null) {
            val bundle = intent.getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE)
            if (bundle != null) {
                options = bundle.getParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS)
            }
        }
        if (options == null) {
            options = ImageOptions()
            if (attrs != null) {


                val ta = context.obtainStyledAttributes(attrs, R.styleable.CropImageView, 0, 0)
                try {
                    options.fixAspectRatio = ta.getBoolean(
                        R.styleable.CropImageView_cropFixAspectRatio,
                        options.fixAspectRatio
                    )
                    options.aspectRatioX = ta.getInteger(
                        R.styleable.CropImageView_cropAspectRatioX,
                        options.aspectRatioX
                    )
                    options.aspectRatioY = ta.getInteger(
                        R.styleable.CropImageView_cropAspectRatioY,
                        options.aspectRatioY
                    )
                    options.scaleType = ScaleType.values()[ta.getInt(
                        R.styleable.CropImageView_cropScaleType,
                        options.scaleType.ordinal
                    )]
                    options.autoZoomEnabled = ta.getBoolean(
                        R.styleable.CropImageView_cropAutoZoomEnabled,
                        options.autoZoomEnabled
                    )
                    options.multiTouchEnabled = ta.getBoolean(
                        R.styleable.CropImageView_cropMultiTouchEnabled, options.multiTouchEnabled
                    )
                    options.maxZoom =
                        ta.getInteger(R.styleable.CropImageView_cropMaxZoom, options.maxZoom)
                    options.cropShape = CropShape.values()[ta.getInt(
                        R.styleable.CropImageView_cropShape,
                        options.cropShape.ordinal
                    )]

                    Log.d("TAG", "check the shape:${options.cropShape}")



                    options.guidelines = Guidelines.values()[ta.getInt(
                        R.styleable.CropImageView_cropGuidelines, options.guidelines.ordinal
                    )]
                    options.snapRadius = ta.getDimension(
                        R.styleable.CropImageView_cropSnapRadius,
                        options.snapRadius
                    )
                    options.touchRadius = ta.getDimension(
                        R.styleable.CropImageView_cropTouchRadius,
                        options.touchRadius
                    )
                    options.initialCropWindowPaddingRatio = ta.getFloat(
                        R.styleable.CropImageView_cropInitialCropWindowPaddingRatio,
                        options.initialCropWindowPaddingRatio
                    )
                    options.borderLineThickness = ta.getDimension(
                        R.styleable.CropImageView_cropBorderLineThickness,
                        options.borderLineThickness
                    )
                    options.borderLineColor = ta.getInteger(
                        R.styleable.CropImageView_cropBorderLineColor,
                        options.borderLineColor
                    )


                    options.borderCornerThickness = ta.getDimension(
                        R.styleable.CropImageView_cropBorderCornerThickness,
                        options.borderCornerThickness
                    )
                    options.borderCornerOffset = ta.getDimension(
                        R.styleable.CropImageView_cropBorderCornerOffset, options.borderCornerOffset
                    )
                    options.borderCornerLength = ta.getDimension(
                        R.styleable.CropImageView_cropBorderCornerLength, options.borderCornerLength
                    )
                    options.borderCornerColor = ta.getInteger(
                        R.styleable.CropImageView_cropBorderCornerColor, options.borderCornerColor
                    )
                    options.guidelinesThickness = ta.getDimension(
                        R.styleable.CropImageView_cropGuidelinesThickness,
                        options.guidelinesThickness
                    )
                    options.guidelinesColor = ta.getInteger(
                        R.styleable.CropImageView_cropGuidelinesColor,
                        options.guidelinesColor
                    )

                    options.backgroundColor = ta.getInteger(
                        R.styleable.CropImageView_cropBackgroundColor,
                        options.backgroundColor
                    )

                    options.showCropOverlay = ta.getBoolean(
                        R.styleable.CropImageView_cropShowCropOverlay,
                        mShowCropOverlay
                    )
                    options.showProgressBar = ta.getBoolean(
                        R.styleable.CropImageView_cropShowProgressBar,
                        mShowProgressBar
                    )
                    options.borderCornerThickness = ta.getDimension(
                        R.styleable.CropImageView_cropBorderCornerThickness,
                        options.borderCornerThickness
                    )
                    options.minCropWindowWidth = ta.getDimension(
                        R.styleable.CropImageView_cropMinCropWindowWidth,
                        options.minCropWindowWidth.toFloat()
                    ).toInt()
                    options.minCropWindowHeight = ta.getDimension(
                        R.styleable.CropImageView_cropMinCropWindowHeight,
                        options.minCropWindowHeight.toFloat()
                    ).toInt()
                    options.minCropResultWidth = ta.getFloat(
                        R.styleable.CropImageView_cropMinCropResultWidthPX,
                        options.minCropResultWidth.toFloat()
                    ).toInt()
                    options.minCropResultHeight = ta.getFloat(
                        R.styleable.CropImageView_cropMinCropResultHeightPX,
                        options.minCropResultHeight.toFloat()
                    ).toInt()
                    options.maxCropResultWidth = ta.getFloat(
                        R.styleable.CropImageView_cropMaxCropResultWidthPX,
                        options.maxCropResultWidth.toFloat()
                    ).toInt()
                    options.maxCropResultHeight = ta.getFloat(
                        R.styleable.CropImageView_cropMaxCropResultHeightPX,
                        options.maxCropResultHeight.toFloat()
                    ).toInt()
                    options.flipHorizontally = ta.getBoolean(
                        R.styleable.CropImageView_cropFlipHorizontally, options.flipHorizontally
                    )
                    options.flipVertically = ta.getBoolean(
                        R.styleable.CropImageView_cropFlipHorizontally,
                        options.flipVertically
                    )
                    isSaveBitmapToInstanceState = ta.getBoolean(
                        R.styleable.CropImageView_cropSaveBitmapToInstanceState,
                        isSaveBitmapToInstanceState
                    )

                    // if aspect ratio is set then set fixed to true
                    if (ta.hasValue(R.styleable.CropImageView_cropAspectRatioX)
                        && ta.hasValue(R.styleable.CropImageView_cropAspectRatioX)
                        && !ta.hasValue(R.styleable.CropImageView_cropFixAspectRatio)
                    ) {
                        options.fixAspectRatio = true
                    }
                } finally {
                    ta.recycle()
                }
            }
        }
        options.validate()
        mScaleType = options.scaleType
        mAutoZoomEnabled = options.autoZoomEnabled
        mMaxZoom = options.maxZoom
        mShowCropOverlay = options.showCropOverlay
        mShowProgressBar = options.showProgressBar
        mFlipHorizontally = options.flipHorizontally
        mFlipVertically = options.flipVertically
        val inflater = LayoutInflater.from(context)

        val v = inflater.inflate(R.layout.crop_image_view, this, true)
        mImageView = v.findViewById(R.id.ImageView_image)
        mImageView.scaleType = ImageView.ScaleType.MATRIX
        mCropOverlayView = v.findViewById(R.id.CropOverlayView)

        mCropOverlayView.setCropWindowChangeListener(CropOverlayView.CropWindowChangeListener { inProgress ->
            val listener = mOnCropOverlayReleasedListener
            if (listener != null && !inProgress) {
                listener.onCropOverlayReleased(cropRect)
            }
            val movedListener = mOnSetCropOverlayMovedListener
            if (movedListener != null && inProgress) {
                movedListener.onCropOverlayMoved(cropRect)
            }
        })


        mCropOverlayView.setInitialAttributeValues(options)
        mProgressBar = v.findViewById(R.id.CropProgressBar)
        progressbarset()
    }

    /** Get the scale type of the image in the crop view.  */
    /** Set the scale type of the image in the crop view  */

    var scaleType: ScaleType
        get() = mScaleType
        set(scaleType) {
            if (scaleType != mScaleType) {
                mScaleType = scaleType
                mZoom = 1f
                mZoomOffsetY = 0f
                mZoomOffsetX = mZoomOffsetY
                mCropOverlayView!!.resetCropOverlayView()
                requestLayout()
            }
        }

    var cropShape: CropShape?
        get() = mCropOverlayView!!.cropShape
        set(cropShape) {
            mCropOverlayView!!.cropShape = cropShape
        }

    /** if auto-zoom functionality is enabled. default: true.  */
    /** Set auto-zoom functionality to enabled/disabled.  */

    var isAutoZoomEnabled: Boolean
        get() = mAutoZoomEnabled
        set(autoZoomEnabled) {
            if (mAutoZoomEnabled != autoZoomEnabled) {
                mAutoZoomEnabled = autoZoomEnabled
                handleCropWindowChanged(false, false)
                mCropOverlayView!!.invalidate()
            }
        }

    /** Set multi touch functionality to enabled/disabled.  */

    fun setMultiTouchEnabled(multiTouchEnabled: Boolean) {
        if (mCropOverlayView!!.setMultiTouchEnabled(multiTouchEnabled)) {
            handleCropWindowChanged(inProgress = false, animate = false)
            mCropOverlayView.invalidate()
        }
    }
    /** The max zoom allowed during cropping.  */
    /** The max zoom allowed during cropping.  */
    var maxZoom: Int
        get() = mMaxZoom
        set(maxZoom) {
            if (mMaxZoom != maxZoom && maxZoom > 0) {
                mMaxZoom = maxZoom
                handleCropWindowChanged(false, false)
                mCropOverlayView!!.invalidate()
            }
        }

    /**
     * the min size the resulting cropping image is allowed to be, affects the cropping window limits
     * (in pixels).<br></br>
     */
    fun setMinCropResultSize(minCropResultWidth: Int, minCropResultHeight: Int) {
        mCropOverlayView!!.setMinCropResultSize(minCropResultWidth, minCropResultHeight)
    }

    /**
     * the max size the resulting cropping image is allowed to be, affects the cropping window limits
     * (in pixels).<br></br>
     */
    fun setMaxCropResultSize(maxCropResultWidth: Int, maxCropResultHeight: Int) {
        mCropOverlayView!!.setMaxCropResultSize(maxCropResultWidth, maxCropResultHeight)
    }


    var rotatedDegrees: Int
        get() = mDegreesRotated
        set(degrees) {
            if (mDegreesRotated != degrees) {
                rotateImage(degrees - mDegreesRotated)
            }
        }


    val isFixAspectRatio: Boolean
        get() = mCropOverlayView!!.isFixAspectRatio


    fun setFixedAspectRatio(fixAspectRatio: Boolean) {
        mCropOverlayView!!.setFixedAspectRatio(fixAspectRatio)
    }
    /** whether the image should be flipped horizontally  */
    /** Sets whether the image should be flipped horizontally  */
    var isFlippedHorizontally: Boolean
        get() = mFlipHorizontally
        set(flipHorizontally) {
            if (mFlipHorizontally != flipHorizontally) {
                mFlipHorizontally = flipHorizontally
                applyImageMatrix(width, height, true, animate = false)
            }
        }
    /** whether the image should be flipped vertically  */
    /** Sets whether the image should be flipped vertically  */
    var isFlippedVertically: Boolean
        get() = mFlipVertically
        set(flipVertically) {
            if (mFlipVertically != flipVertically) {
                mFlipVertically = flipVertically
                applyImageMatrix(width, height, center = true, animate = false)
            }
        }
    /** Get the current guidelines option set.  */
    /**
     * Sets the guidelines for the CropOverlayView to be either on, off, or to show when resizing the
     * application.
     */
    var guidelines: Guidelines?
        get() = mCropOverlayView!!.guidelines
        set(guidelines) {
            mCropOverlayView!!.guidelines = guidelines
        }

    val aspectRatio: Pair<Int, Int>
        get() = Pair(mCropOverlayView!!.aspectRatioX, mCropOverlayView.aspectRatioY)


    fun setAspectRatio(aspectRatioX: Int, aspectRatioY: Int) {
        mCropOverlayView!!.aspectRatioX = aspectRatioX
        mCropOverlayView.aspectRatioY = aspectRatioY
        setFixedAspectRatio(true)
    }

    fun clearAspectRatio() {
        mCropOverlayView!!.aspectRatioX = 1
        mCropOverlayView.aspectRatioY = 1
        setFixedAspectRatio(false)
    }


    /**
     * An edge of the crop window will snap to the corresponding edge of a specified bounding box when
     * the crop window edge is less than or equal to this distance (in pixels) away from the bounding
     * box edge. (default: 3dp)
     */

    fun setSnapRadius(snapRadius: Float) {
        if (snapRadius >= 0) {
            mCropOverlayView!!.setSnapRadius(snapRadius)
        }
    }
    /**
     * if to show progress bar when image async loading/cropping is in progress.<br></br>
     * default: true, disable to provide custom progress bar UI.
     */
    /**
     * if to show progress bar when image async loading/cropping is in progress.<br></br>
     * default: true, disable to provide custom progress bar UI.
     */
    var isShowProgressBar: Boolean
        get() = mShowProgressBar
        set(showProgressBar) {
            if (mShowProgressBar != showProgressBar) {
                mShowProgressBar = showProgressBar
                progressbarset()
            }
        }
    /**
     * if to show crop overlay UI what contains the crop window UI surrounded by background over the
     * cropping image.<br></br>
     * default: true, may disable for animation or frame transition.
     */
    /**
     * if to show crop overlay UI what contains the crop window UI surrounded by background over the
     * cropping image.<br></br>
     * default: true, may disable for animation or frame transition.
     */
    var isShowCropOverlay: Boolean
        get() = mShowCropOverlay
        set(showCropOverlay) {
            if (mShowCropOverlay != showCropOverlay) {
                mShowCropOverlay = showCropOverlay
                setCropOverlayVisibility()
            }
        }
    /** Returns the integer of the imageResource  */
    /**
     * Sets a Drawable as the content of the CropImageView.
     *
     * @param resId the drawable resource ID to set
     */
    var imageResource: Int
        get() = mImageResource
        set(resId) {
            if (resId != 0) {
                mCropOverlayView!!.initialCropWindowRect = null
                val bitmap = BitmapFactory.decodeResource(resources, resId)
                setBitmap(bitmap, resId, null, 1, 0)
            }
        }

    /**
     * Gets the source Bitmap's dimensions. This represents the largest possible crop rectangle.
     *
     * @return a Rect instance dimensions of the source Bitmap
     */
    val wholeImageRect: Rect?
        get() {
            val loadedSampleSize = mLoadedSampleSize
            val bitmap = mBitmap ?: return null
            val orgWidth = bitmap.width * loadedSampleSize
            val orgHeight = bitmap.height * loadedSampleSize
            return Rect(0, 0, orgWidth, orgHeight)
        }// get the points of the crop rectangle adjusted to source bitmap

    // get the rectangle for the points (it may be larger than original if rotation is not stright)
    /**
     * Set the crop window position and size to the given rectangle.<br></br>
     * Image to crop must be first set before invoking this, for async - after complete callback.
     *
     * @param rect window rectangle (position and size) relative to source bitmap
     */
    /**
     * Gets the crop window's position relative to the source Bitmap (not the image displayed in the
     * CropImageView) using the original image rotation.
     *
     * @return a Rect instance containing cropped area boundaries of the source Bitmap
     */
    var cropRect: Rect?
        get() {
            val loadedSampleSize = mLoadedSampleSize
            val bitmap = mBitmap ?: return null

            // get the points of the crop rectangle adjusted to source bitmap
            val points = cropPoints
            val orgWidth = bitmap.width * loadedSampleSize
            val orgHeight = bitmap.height * loadedSampleSize

            // get the rectangle for the points (it may be larger than original if rotation is not stright)
            return BitmapUtils.getRectFromPoints(
                points,
                orgWidth,
                orgHeight,
                mCropOverlayView!!.isFixAspectRatio,
                mCropOverlayView.aspectRatioX,
                mCropOverlayView.aspectRatioY
            )
        }
        set(rect) {
            mCropOverlayView!!.initialCropWindowRect = rect
        }

    /**
     * Gets the crop window's position relative to the parent's view at screen.
     *
     * @return a Rect instance containing cropped area boundaries of the source Bitmap
     */
    val cropWindowRect: RectF?
        get() = mCropOverlayView?.cropWindowRect// Get crop window position relative to the displayed image.

    /**
     * Gets the 4 points of crop window's position relative to the source Bitmap (not the image
     * displayed in the CropImageView) using the original image rotation.<br></br>
     * Note: the 4 points may not be a rectangle if the image was rotates to NOT stright angle (!=
     * 90/180/270).
     *
     * @return 4 points (x0,y0,x1,y1,x2,y2,x3,y3) of cropped area boundaries
     */
    val cropPoints: FloatArray
        get() {

            // Get crop window position relative to the displayed image.
            val cropWindowRect = mCropOverlayView!!.cropWindowRect
            val points = floatArrayOf(
                cropWindowRect!!.left,
                cropWindowRect.top,
                cropWindowRect.right,
                cropWindowRect.top,
                cropWindowRect.right,
                cropWindowRect.bottom,
                cropWindowRect.left,
                cropWindowRect.bottom
            )
            mImageMatrix.invert(mImageInverseMatrix)
            mImageInverseMatrix.mapPoints(points)
            for (i in points.indices) {
                points[i] *= mLoadedSampleSize.toFloat()
            }
            return points

//
        }

    /** Reset crop window to initial rectangle.  */
    fun resetCropRect() {
        mZoom = 1f
        mZoomOffsetX = 0f
        mZoomOffsetY = 0f
        mDegreesRotated = mInitialDegreesRotated
        mFlipHorizontally = false
        mFlipVertically = false
        applyImageMatrix(width, height, false, false)
        mCropOverlayView!!.resetCropWindowRect()
    }

    /**
     * Gets the cropped image based on the current crop window.
     *
     * @return a new Bitmap representing the cropped image
     */
    val croppedImage: Bitmap?
        get() = getCroppedImage(0, 0, RequestSizeOptions.NONE)

    /**
     * Gets the cropped image based on the current crop window.<br></br>
     * Uses [RequestSizeOptions.RESIZE_INSIDE] option.
     *
     * @param reqWidth the width to resize the cropped image to
     * @param reqHeight the height to resize the cropped image to
     * @return a new Bitmap representing the cropped image
     */
    fun getCroppedImage(reqWidth: Int, reqHeight: Int): Bitmap? {
        return getCroppedImage(reqWidth, reqHeight, RequestSizeOptions.RESIZE_INSIDE)
    }

    /**
     * Gets the cropped image based on the current crop window.<br></br>
     *
     * @param reqWidth the width to resize the cropped image to (see options)
     * @param reqHeight the height to resize the cropped image to (see options)
     * @param options the resize method to use, see its documentation
     * @return a new Bitmap representing the cropped image
     */
    fun getCroppedImage(reqWidth: Int, reqHeight: Int, options: RequestSizeOptions): Bitmap? {
        var reqWidth = reqWidth
        var reqHeight = reqHeight
        var croppedBitmap: Bitmap? = null
        if (mBitmap != null) {
            mImageView.clearAnimation()
            reqWidth = if (options != RequestSizeOptions.NONE) reqWidth else 0
            reqHeight = if (options != RequestSizeOptions.NONE) reqHeight else 0
            croppedBitmap = if (imageUri != null
                && (mLoadedSampleSize > 1 || options == RequestSizeOptions.SAMPLING)
            ) {
                val orgWidth = mBitmap!!.width * mLoadedSampleSize
                val orgHeight = mBitmap!!.height * mLoadedSampleSize
                val bitmapSampled = BitmapUtils.cropBitmap(
                    context,
                    imageUri!!,
                    cropPoints,
                    mDegreesRotated,
                    orgWidth,
                    orgHeight,
                    mCropOverlayView!!.isFixAspectRatio,
                    mCropOverlayView.aspectRatioX,
                    mCropOverlayView.aspectRatioY,
                    reqWidth,
                    reqHeight,
                    mFlipHorizontally,
                    mFlipVertically
                )
                bitmapSampled.bitmap
            } else {
                BitmapUtils.cropBitmapObjectHandleOOM(
                    mBitmap!!,
                    cropPoints,
                    mDegreesRotated,
                    mCropOverlayView!!.isFixAspectRatio,
                    mCropOverlayView.aspectRatioX,
                    mCropOverlayView.aspectRatioY,
                    mFlipHorizontally,
                    mFlipVertically
                ).bitmap
            }
            croppedBitmap = BitmapUtils.resizeBitmap(croppedBitmap!!, reqWidth, reqHeight, options)
        }
        return croppedBitmap
    }

    /**
     * Gets the cropped image based on the current crop window.<br></br>
     * The result will be invoked to listener set by [ ][.setOnCropImageCompleteListener].
     */
    val croppedImageAsync: Unit
        get() {
            getCroppedImageAsync(0, 0, RequestSizeOptions.NONE)
        }

    /**
     * Gets the cropped image based on the current crop window.<br></br>
     * Uses [RequestSizeOptions.RESIZE_INSIDE] option.<br></br>
     * The result will be invoked to listener set by [ ][.setOnCropImageCompleteListener].
     *
     * @param reqWidth the width to resize the cropped image to
     * @param reqHeight the height to resize the cropped image to
     */
    fun getCroppedImageAsync(reqWidth: Int, reqHeight: Int) {
        getCroppedImageAsync(reqWidth, reqHeight, RequestSizeOptions.RESIZE_INSIDE)
    }

    /**
     * Gets the cropped image based on the current crop window.<br></br>
     * The result will be invoked to listener set by [ ][.setOnCropImageCompleteListener].
     *
     * @param reqWidth the width to resize the cropped image to (see options)
     * @param reqHeight the height to resize the cropped image to (see options)
     * @param options the resize method to use, see its documentation
     */
    fun getCroppedImageAsync(reqWidth: Int, reqHeight: Int, options: RequestSizeOptions) {
        requireNotNull(mOnCropImageCompleteListener) { "mOnCropImageCompleteListener is not set" }
        startCropWorkerTask(reqWidth, reqHeight, options, null, null, 0)
    }

    /**
     * Save the cropped image based on the current crop window to the given uri.<br></br>
     * Uses [RequestSizeOptions.RESIZE_INSIDE] option.<br></br>
     * The result will be invoked to listener set by [ ][.setOnCropImageCompleteListener].
     *
     * @param saveUri the Android Uri to save the cropped image to
     * @param saveCompressFormat the compression format to use when writing the image
     * @param saveCompressQuality the quality (if applicable) to use when writing the image (0 - 100)
     * @param reqWidth the width to resize the cropped image to
     * @param reqHeight the height to resize the cropped image to
     */
    @JvmName("saveCroppedImageAsync1")
    fun saveCroppedImageAsync(
        saveUri: Uri?,
        saveCompressFormat: Bitmap.CompressFormat?,
        saveCompressQuality: Int,
        reqWidth: Int,
        reqHeight: Int,
    ) {
        saveCroppedImageAsync(
            saveUri,
            saveCompressFormat,
            saveCompressQuality,
            reqWidth,
            reqHeight,
            RequestSizeOptions.RESIZE_INSIDE,
            mFlipVertically,
            mFlipHorizontally
        )
    }
    /**
     * Save the cropped image based on the current crop window to the given uri.<br></br>
     * The result will be invoked to listener set by [ ][.setOnCropImageCompleteListener].
     *
     * @param saveUri the Android Uri to save the cropped image to
     * @param saveCompressFormat the compression format to use when writing the image
     * @param saveCompressQuality the quality (if applicable) to use when writing the image (0 - 100)
     * @param reqWidth the width to resize the cropped image to (see options)
     * @param reqHeight the height to resize the cropped image to (see options)
     * @param options the resize method to use, see its documentation
     */
    /**
     * Save the cropped image based on the current crop window to the given uri.<br></br>
     * Uses JPEG image compression with 90 compression quality.<br></br>
     * The result will be invoked to listener set by [ ][.setOnCropImageCompleteListener].
     *
     * @param saveUri the Android Uri to save the cropped image to
     */
    /**
     * Save the cropped image based on the current crop window to the given uri.<br></br>
     * The result will be invoked to listener set by [ ][.setOnCropImageCompleteListener].
     *
     * @param saveUri the Android Uri to save the cropped image to
     * @param saveCompressFormat the compression format to use when writing the image
     * @param saveCompressQuality the quality (if applicable) to use when writing the image (0 - 100)
     */
    @JvmOverloads
    fun saveCroppedImageAsync(
        saveUri: Uri?,
        saveCompressFormat: Bitmap.CompressFormat? = Bitmap.CompressFormat.JPEG,
        saveCompressQuality: Int = 90,
        reqWidth: Int = 0,
        reqHeight: Int = 0,
        options: RequestSizeOptions = RequestSizeOptions.NONE,
        mFlipVertically: Boolean,
        mFlipHotizontally: Boolean,


        ) {


        requireNotNull(mOnCropImageCompleteListener) { "mOnCropImageCompleteListener is not set" }
        startCropWorkerTask(
            reqWidth, reqHeight, options, saveUri, saveCompressFormat, saveCompressQuality
        )

        Log.d("TAG", "saveCroppedImageAsync: check the logogogog:::")
    }

    /** Set the callback t  */
    fun setOnSetCropOverlayReleasedListener(listener: OnSetCropOverlayReleasedListener?) {
        mOnCropOverlayReleasedListener = listener
    }

    /** Set the callback when the cropping is moved  */
    fun setOnSetCropOverlayMovedListener(listener: OnSetCropOverlayMovedListener?) {
        mOnSetCropOverlayMovedListener = listener
    }

    /** Set the callback when the crop window is changed  */
    fun setOnCropWindowChangedListener(listener: OnSetCropWindowChangeListener?) {
        mOnSetCropWindowChangeListener = listener
    }

    /**
     * Set the callback to be invoked when image async loading ([.setImageUriAsync]) is
     * complete (successful or failed).
     */
    fun setOnSetImageUriCompleteListener(listener: OnSetImageUriCompleteListener?) {
        mOnSetImageUriCompleteListener = listener
    }

    /**
     * Set the callback to be invoked when image async cropping image ([.getCroppedImageAsync]
     * or [.saveCroppedImageAsync]) is complete (successful or failed).
     */
    fun setOnCropImageCompleteListener(listener: OnCropImageCompleteListener?) {
        mOnCropImageCompleteListener = listener
    }

    /**
     * Sets a Bitmap as the content of the CropImageView.
     *
     * @param bitmap the Bitmap to set
     */
    fun setImageBitmap(bitmap: Bitmap?) {
        mCropOverlayView!!.initialCropWindowRect = null
        setBitmap(bitmap, 0, null, 1, 0)
    }

    /**
     * Sets a Bitmap and initializes the image rotation according to the EXIT data.<br></br>
     * <br></br>
     * The EXIF can be retrieved by doing the following: `
     * ExifInterface exif = new ExifInterface(path);`
     *
     * @param bitmap the original bitmap to set; if null, this
     * @param exif the EXIF information about this bitmap; may be null
     */
    fun setImageBitmap(bitmap: Bitmap?, exif: ExifInterface?) {
        val setBitmap: Bitmap?
        var degreesRotated = 0
        if (bitmap != null && exif != null) {
            val result = BitmapUtils.rotateBitmapByExif(bitmap, exif)
            setBitmap = result.bitmap
            degreesRotated = result.degrees
            mInitialDegreesRotated = result.degrees
        } else {
            setBitmap = bitmap
        }
        mCropOverlayView!!.initialCropWindowRect = null
        setBitmap(setBitmap, 0, null, 1, degreesRotated)
    }

    /**
     * Sets a bitmap loaded from the given Android URI as the content of the CropImageView.<br></br>
     * Can be used with URI from gallery or camera source.<br></br>
     * Will rotate the image by exif data.<br></br>
     *
     * @param uri the URI to load the image from
     */
    fun setImageUriAsync(uri: Uri?) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                if (uri != null) {
                    val currentTask =
                        if (mBitmapLoading != null) mBitmapLoading!!.get() else null
                    currentTask?.cancel(true)

                    // either no existing task is working or we canceled it, need to load new URI
                    clearImageInt()
                    mRestoreCropWindowRect = null
                    mRestoreDegreesRotated = 0
                    mCropOverlayView!!.initialCropWindowRect = null
                    mBitmapLoading = WeakReference(BitmapLoading(this@CropImageView, uri))
                    mBitmapLoading!!.get()!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                }

            }
            withContext(Dispatchers.Main) {
                progressbarset()

            }
        }

    }

    /** Clear the current image set for cropping.  */
    fun clearImage() {
        clearImageInt()
        mCropOverlayView!!.initialCropWindowRect = null
    }

    /**
     * Rotates image by the specified number of degrees clockwise.<br></br>
     * Negative values represent counter-clockwise rotations.
     *
     * @param degrees Integer specifying the number of degrees to rotate.
     */
    fun rotateImage(degrees: Int) {
        var degrees = degrees
        val TAG = "sdfsfdsfsdfdsfdsfds"
        Log.d(TAG, "rotateImage: $degrees")
        if (mBitmap != null) {
            Log.d(TAG, "rotateImage: bitmap is not null")
            // Force degrees to be a non-zero value between 0 and 360 (inclusive)
            degrees = if (degrees < 0) {
                Log.d(TAG, "rotateImage: degrees are less then 0")
                degrees % 360 + 360
//                Log.d(TAG, "rotateImage: degrees less then 0 = $degrees")
            } else {
                Log.d(TAG, "rotateImage: degrees more then 0")
                degrees % 360
//                Log.d(TAG, "rotateImage: degrees more then 0 = $degrees")
            }
            Log.d(TAG, "rotateImage: degrees are = $degrees")
            val flipAxes =
                (!mCropOverlayView!!.isFixAspectRatio && (degrees > 45 && degrees < 135 || degrees > 215 && degrees < 305))
            Log.d(TAG, "rotateImage: check flipAxes = $flipAxes")
            Log.d(TAG, "rotateImage: crop window rect = Left = ${mCropOverlayView.cropWindowRect.left}")
            Log.d(TAG, "rotateImage: crop window rect = Top = ${mCropOverlayView.cropWindowRect.top}")
            Log.d(TAG, "rotateImage: crop window rect = Right = ${mCropOverlayView.cropWindowRect.right}")
            Log.d(TAG, "rotateImage: crop window rect = Bottom = ${mCropOverlayView.cropWindowRect.bottom}")

            BitmapUtils.RECT.set(mCropOverlayView.cropWindowRect!!)

            Log.d(TAG, "rotateImage: BitmapUtils.RECT = Left ${BitmapUtils.RECT.left}")
            Log.d(TAG, "rotateImage: BitmapUtils.RECT = Top ${BitmapUtils.RECT.top}")
            Log.d(TAG, "rotateImage: BitmapUtils.RECT = Right ${BitmapUtils.RECT.right}")
            Log.d(TAG, "rotateImage: BitmapUtils.RECT = Bottom ${BitmapUtils.RECT.bottom}")
            Log.d(TAG, "rotateImage: mCropOverlayView ratio  x ${mCropOverlayView.aspectRatioX}")
            Log.d(TAG, "rotateImage: mCropOverlayView ratio  y ${mCropOverlayView.aspectRatioY}")

            var halfWidth = (if (flipAxes) BitmapUtils.RECT.height() else BitmapUtils.RECT.width()) / 2f
            var halfHeight = (if (flipAxes) BitmapUtils.RECT.width() else BitmapUtils.RECT.height()) / 2f
            if (flipAxes) {
                val isFlippedHorizontally = mFlipHorizontally
                mFlipHorizontally = mFlipVertically
                mFlipVertically = isFlippedHorizontally
            }
            mImageMatrix.invert(mImageInverseMatrix)
            BitmapUtils.POINTS[0] = BitmapUtils.RECT.centerX()
            BitmapUtils.POINTS[1] = BitmapUtils.RECT.centerY()
            BitmapUtils.POINTS[2] = 0f
            BitmapUtils.POINTS[3] = 0f
            BitmapUtils.POINTS[4] = 1f
            BitmapUtils.POINTS[5] = 0f
            mImageInverseMatrix.mapPoints(BitmapUtils.POINTS)

            // This is valid because degrees is not negative.
            mDegreesRotated = (mDegreesRotated + degrees) % 360
            applyImageMatrix(width, height, true, false)

            // adjust the zoom so the crop window size remains the same even after image scale change
            mImageMatrix.mapPoints(BitmapUtils.POINTS2, BitmapUtils.POINTS)
//            mZoom /= Math.sqrt(
//                Math.pow((BitmapUtils.POINTS2[4] - BitmapUtils.POINTS2[2]).toDouble(), 2.0)
//                        + Math.pow(
//                    (BitmapUtils.POINTS2[5] - BitmapUtils.POINTS2[3]).toDouble(),
//                    2.0
//                )
//            ).toFloat()
//            mZoom = Math.max(mZoom, 1f)
            applyImageMatrix(width, height, true, false)
            mImageMatrix.mapPoints(BitmapUtils.POINTS2, BitmapUtils.POINTS)

            // adjust the width/height by the changes in scaling to the image
            val change = Math.sqrt(
                Math.pow((BitmapUtils.POINTS2[4] - BitmapUtils.POINTS2[2]).toDouble(), 2.0)
                        + Math.pow(
                    (BitmapUtils.POINTS2[5] - BitmapUtils.POINTS2[3]).toDouble(),
                    2.0
                )
            )
            halfWidth *= change.toFloat()
            halfHeight *= change.toFloat()

            // calculate the new crop window rectangle to center in the same location and have proper
            // width/height
            BitmapUtils.RECT[BitmapUtils.POINTS2[0] - halfWidth, BitmapUtils.POINTS2[1] - halfHeight, BitmapUtils.POINTS2[0] + halfWidth] =
                BitmapUtils.POINTS2[1] + halfHeight
            mCropOverlayView.resetCropOverlayView()
            mCropOverlayView.cropWindowRect = BitmapUtils.RECT
            applyImageMatrix(width, height, true, false)
            handleCropWindowChanged(false, false)

            // make sure the crop window rectangle is within the cropping image bounds after all the
            // changes
            mCropOverlayView.fixCurrentCropWindowRect()
        }
    }

    /** Flips the image horizontally.  */
    fun flipImageHorizontally() {
        mFlipHorizontally = !mFlipHorizontally
        applyImageMatrix(width, height, true, false)
    }

    /** Flips the image vertically.  */
    fun flipImageVertically() {
        mFlipVertically = !mFlipVertically
        applyImageMatrix(width, height, true, false)

    }

    fun fliphorizontal() {
        mFlipHorizontally = !mFlipHorizontally
        ImageFlipper.flip(mImageView, FlipDirection.HORIZONTAL)

    }

    fun flipVetical() {
        mFlipVertically = !mFlipVertically
        ImageFlipper.flip(mImageView, FlipDirection.VERTICAL)

    }


    // region: Private methods
    fun onSetImageUriAsyncComplete(result: BitmapLoading.Result) {
        mBitmapLoading = null

        progressbarset()
        if (result.error == null) {
            mInitialDegreesRotated = result.degreesRotated
            setBitmap(result.bitmap, 0, result.uri, result.loadSampleSize, result.degreesRotated)
        }
        val listener = mOnSetImageUriCompleteListener
        if (listener != null) {
            listener.onSetImageUriComplete(this, result.uri, result.error)
            Log.d("TAG", "onSetImageUriAsyncComplete: check result:" + result.uri)
            Log.d("TAG", "onSetImageUriAsyncComplete: check result:" + result.error)
        }
    }

    fun onImageCroppingAsyncComplete(result: BitmapCropping.Result) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                mBitmapCropping = null
                val listener = mOnCropImageCompleteListener
                if (listener != null) {
                    val cropResult = CropResult(
                        mBitmap,
                        imageUri,
                        result.bitmap,
                        result.uri,
                        result.error,
                        cropPoints,
                        cropRect,
                        wholeImageRect,
                        rotatedDegrees,
                        result.sampleSize,
                    )
                    listener.onCropImageComplete(this@CropImageView, cropResult)
                }
            }
            withContext(Dispatchers.Main) {
                progressbarset()

            }
        }

    }


    /**
     * Set the given bitmap to be used in for cropping<br></br>
     * Optionally clear full if the bitmap is new, or partial clear if the bitmap has been
     * manipulated.
     */
    private fun setBitmap(
        bitmap: Bitmap?,
        imageResource: Int,
        imageUri: Uri?,
        loadSampleSize: Int,
        degreesRotated: Int,
    ) {
        if (mBitmap == null || mBitmap != bitmap) {
            mImageView.clearAnimation()
            clearImageInt()
            mBitmap = bitmap
            mImageView.setImageBitmap(mBitmap)
            this.imageUri = imageUri
            mImageResource = imageResource
            mLoadedSampleSize = loadSampleSize
            mDegreesRotated = degreesRotated
            applyImageMatrix(width, height, true, false)
            if (mCropOverlayView != null) {
                mCropOverlayView.resetCropOverlayView()
                setCropOverlayVisibility()
            }
        }
    }

    /**
     * Clear the current image set for cropping.<br></br>
     * Full clear will also clear the data of the set image like Uri or Resource id while partial
     * clear will only clear the bitmap and recycle if required.
     */
    private fun clearImageInt() {

        // if we allocated the bitmap, release it as fast as possible
        if (mBitmap != null && (mImageResource > 0 || imageUri != null)) {
            mBitmap!!.recycle()
        }
        mBitmap = null

        // clean the loaded image flags for new image
        mImageResource = 0
        imageUri = null
        mLoadedSampleSize = 1
        mDegreesRotated = 0
        mZoom = 1f
        mZoomOffsetX = 0f
        mZoomOffsetY = 0f
        mImageMatrix.reset()
        mSaveInstanceStateBitmapUri = null
        mImageView.setImageBitmap(null)
        setCropOverlayVisibility()
    }

    /**
     * Gets the cropped image based on the current crop window.<br></br>
     * If (reqWidth,reqHeight) is given AND image is loaded from URI cropping will try to use sample
     * size to fit in the requested width and height down-sampling if possible - optimization to get
     * best size to quality.<br></br>
     * The result will be invoked to listener set by [ ][.setOnCropImageCompleteListener].
     *
     * @param reqWidth the width to resize the cropped image to (see options)
     * @param reqHeight the height to resize the cropped image to (see options)
     * @param options the resize method to use on the cropped bitmap
     * @param saveUri optional: to save the cropped image to
     * @param saveCompressFormat if saveUri is given, the given compression will be used for saving
     * the image
     * @param saveCompressQuality if saveUri is given, the given quality will be used for the
     * compression.
     */
    fun startCropWorkerTask(
        reqWidth: Int,
        reqHeight: Int,
        options: RequestSizeOptions,
        saveUri: Uri?,
        saveCompressFormat: Bitmap.CompressFormat?,
        saveCompressQuality: Int,

        ) {

        GlobalScope.launch {
            withContext(Dispatchers.IO) {

                var reqWidth = reqWidth
                var reqHeight = reqHeight
                val bitmap = mBitmap
                if (bitmap != null) {
                    mImageView.clearAnimation()
                    val currentTask = if (mBitmapCropping != null) mBitmapCropping!!.get() else null
                    currentTask?.cancel(true)
                    reqWidth = if (options != RequestSizeOptions.NONE) reqWidth else 0
                    reqHeight = if (options != RequestSizeOptions.NONE) reqHeight else 0
                    val orgWidth = bitmap.width * mLoadedSampleSize
                    val orgHeight = bitmap.height * mLoadedSampleSize
                    mBitmapCropping = if (imageUri != null
                        && (mLoadedSampleSize > 1 || options == RequestSizeOptions.SAMPLING)
                    ) {
                        WeakReference(
                            BitmapCropping(
                                this@CropImageView,
                                imageUri,
                                cropPoints,
                                mDegreesRotated,
                                orgWidth,
                                orgHeight,
                                mCropOverlayView!!.isFixAspectRatio,
                                mCropOverlayView.aspectRatioX,
                                mCropOverlayView.aspectRatioY,
                                reqWidth,
                                reqHeight,
                                mFlipHorizontally,
                                mFlipVertically,
                                options,
                                saveUri,
                                saveCompressFormat!!,
                                saveCompressQuality
                            )
                        )
                    } else {
                        WeakReference(
                            BitmapCropping(
                                this@CropImageView,
                                bitmap,
                                cropPoints,
                                mDegreesRotated,
                                mCropOverlayView!!.isFixAspectRatio,
                                mCropOverlayView.aspectRatioX,
                                mCropOverlayView.aspectRatioY,
                                reqWidth,
                                reqHeight,
                                mFlipHorizontally,
                                mFlipVertically,
                                options,
                                saveUri,
                                saveCompressFormat!!,
                                saveCompressQuality
                            )
                        )

                    }
                    mBitmapCropping!!.get()!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                }

            }
            withContext(Dispatchers.Main) {
                progressbarset()

            }
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        if (imageUri == null && mBitmap == null && mImageResource < 1) {
            return super.onSaveInstanceState()
        }
        var imageUri = imageUri

        Log.d("TAG", "onSaveInstanceState: $imageUri")
        if (isSaveBitmapToInstanceState && imageUri == null && mImageResource < 1) {
            imageUri = BitmapUtils.writeTempStateStoreBitmap(
                context, mBitmap!!, mSaveInstanceStateBitmapUri
            )
            mSaveInstanceStateBitmapUri = imageUri
        }
        if (imageUri != null && mBitmap != null) {
            val key = UUID.randomUUID().toString()
            BitmapUtils.mStateBitmap = Pair(key, WeakReference(mBitmap))
            bundle.putString("LOADED_IMAGE_STATE_BITMAP_KEY", key)
        }
        if (mBitmapLoading != null) {
            val task = mBitmapLoading!!.get()
            if (task != null) {
                bundle.putParcelable("LOADING_IMAGE_URI", task.uri)
            }
        }
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        bundle.putParcelable("LOADED_IMAGE_URI", imageUri)
        bundle.putInt("LOADED_IMAGE_RESOURCE", mImageResource)
        bundle.putInt("LOADED_SAMPLE_SIZE", mLoadedSampleSize)
        bundle.putInt("DEGREES_ROTATED", mDegreesRotated)
        bundle.putParcelable("INITIAL_CROP_RECT", mCropOverlayView!!.initialCropWindowRect)
        BitmapUtils.RECT.set(mCropOverlayView.cropWindowRect!!)
        mImageMatrix.invert(mImageInverseMatrix)
        mImageInverseMatrix.mapRect(BitmapUtils.RECT)
        bundle.putParcelable("CROP_WINDOW_RECT", BitmapUtils.RECT)
        bundle.putString("CROP_SHAPE", mCropOverlayView.cropShape!!.name)
        bundle.putBoolean("CROP_AUTO_ZOOM_ENABLED", mAutoZoomEnabled)
        bundle.putInt("CROP_MAX_ZOOM", mMaxZoom)
        bundle.putBoolean("CROP_FLIP_HORIZONTALLY", mFlipHorizontally)
        bundle.putBoolean("CROP_FLIP_VERTICALLY", mFlipVertically)



        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            val bundle = state

            // prevent restoring state if already set by outside code
            if (mBitmapLoading == null && imageUri == null && mBitmap == null && mImageResource == 0) {
                var uri = bundle.getParcelable<Uri>("LOADED_IMAGE_URI")
                if (uri != null) {
                    val key = bundle.getString("LOADED_IMAGE_STATE_BITMAP_KEY")
                    if (key != null) {
                        val stateBitmap =
                            if (BitmapUtils.mStateBitmap != null && BitmapUtils.mStateBitmap!!.first == key) BitmapUtils.mStateBitmap!!.second.get() else null
                        BitmapUtils.mStateBitmap = null
                        if (stateBitmap != null && !stateBitmap.isRecycled) {
                            setBitmap(stateBitmap, 0, uri, bundle.getInt("LOADED_SAMPLE_SIZE"), 0)
                        }
                    }
                    if (imageUri == null) {
                        setImageUriAsync(uri)
                    }
                } else {
                    val resId = bundle.getInt("LOADED_IMAGE_RESOURCE")
                    if (resId > 0) {
                        imageResource = resId
                    } else {
                        uri = bundle.getParcelable("LOADING_IMAGE_URI")
                        uri?.let { setImageUriAsync(it) }
                    }
                }
                mRestoreDegreesRotated = bundle.getInt("DEGREES_ROTATED")
                mDegreesRotated = mRestoreDegreesRotated
                val initialCropRect = bundle.getParcelable<Rect>("INITIAL_CROP_RECT")
                if (initialCropRect != null
                    && (initialCropRect.width() > 0 || initialCropRect.height() > 0)
                ) {
                    mCropOverlayView!!.initialCropWindowRect = initialCropRect
                }
                val cropWindowRect = bundle.getParcelable<RectF>("CROP_WINDOW_RECT")
                if (cropWindowRect != null && (cropWindowRect.width() > 0 || cropWindowRect.height() > 0)) {
                    mRestoreCropWindowRect = cropWindowRect
                }
                mCropOverlayView!!.cropShape = CropShape.valueOf(bundle.getString("CROP_SHAPE")!!)
                mAutoZoomEnabled = bundle.getBoolean("CROP_AUTO_ZOOM_ENABLED")
                mMaxZoom = bundle.getInt("CROP_MAX_ZOOM")
                mFlipHorizontally = bundle.getBoolean("CROP_FLIP_HORIZONTALLY")
                mFlipVertically = bundle.getBoolean("CROP_FLIP_VERTICALLY")


                Log.d("TAG", "onRestoreInstanceState: check the image uri:$mFlipVertically")


            }
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (mBitmap != null) {

            // Bypasses a baffling bug when used within a ScrollView, where heightSize is set to 0.
            if (heightSize == 0) {
                heightSize = mBitmap!!.height
            }
            val desiredWidth: Int
            val desiredHeight: Int
            var viewToBitmapWidthRatio = Double.POSITIVE_INFINITY
            var viewToBitmapHeightRatio = Double.POSITIVE_INFINITY

            // Checks if either width or height needs to be fixed
            if (widthSize < mBitmap!!.width) {
                viewToBitmapWidthRatio = widthSize.toDouble() / mBitmap!!.width.toDouble()
            }
            if (heightSize < mBitmap!!.height) {
                viewToBitmapHeightRatio = heightSize.toDouble() / mBitmap!!.height.toDouble()
            }

            // If either needs to be fixed, choose smallest ratio and calculate from there
            if (viewToBitmapWidthRatio != Double.POSITIVE_INFINITY
                || viewToBitmapHeightRatio != Double.POSITIVE_INFINITY
            ) {
                if (viewToBitmapWidthRatio <= viewToBitmapHeightRatio) {
                    desiredWidth = widthSize
                    desiredHeight = (mBitmap!!.height * viewToBitmapWidthRatio).toInt()
                } else {
                    desiredHeight = heightSize
                    desiredWidth = (mBitmap!!.width * viewToBitmapHeightRatio).toInt()
                }
            } else {
                // Otherwise, the picture is within frame layout bounds. Desired width is simply picture
                // size
                desiredWidth = mBitmap!!.width
                desiredHeight = mBitmap!!.height
            }
            val width = getOnMeasureSpec(widthMode, widthSize, desiredWidth)
            val height = getOnMeasureSpec(heightMode, heightSize, desiredHeight)
            mLayoutWidth = width
            mLayoutHeight = height
            setMeasuredDimension(mLayoutWidth, mLayoutHeight)
        } else {
            setMeasuredDimension(widthSize, heightSize)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (mLayoutWidth > 0 && mLayoutHeight > 0) {
            // Gets original parameters, and creates the new parameters
            val origParams = this.layoutParams
            origParams.width = mLayoutWidth
            origParams.height = mLayoutHeight
            layoutParams = origParams
            if (mBitmap != null) {
                applyImageMatrix((r - l), (b - t), true, false)

                // after state restore we want to restore the window crop, possible only after widget size
                // is known
                if (mRestoreCropWindowRect != null) {
                    if (mRestoreDegreesRotated != mInitialDegreesRotated) {
                        mDegreesRotated = mRestoreDegreesRotated
                        applyImageMatrix((r - l), (b - t), true, false)
                    }
                    mImageMatrix.mapRect(mRestoreCropWindowRect)
                    mCropOverlayView!!.cropWindowRect = mRestoreCropWindowRect
                    handleCropWindowChanged(false, false)
                    mCropOverlayView.fixCurrentCropWindowRect()
                    mRestoreCropWindowRect = null
                } else if (mSizeChanged) {
                    mSizeChanged = false
                    handleCropWindowChanged(false, false)
                }
            } else {
                updateImageBounds(true)
            }
        } else {
            updateImageBounds(true)
        }
    }

    /**
     * Detect size change to handle auto-zoom using [.handleCropWindowChanged]
     * in [.layout].
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mSizeChanged = oldw > 0 && oldh > 0
    }

    /**
     * Handle crop window change to:<br></br>
     * 1. Execute auto-zoom-in/out depending on the area covered of cropping window relative to the
     * available view area.<br></br>
     * 2. Slide the zoomed sub-area if the cropping window is outside of the visible view sub-area.
     * <br></br>
     *
     * @param inProgress is the crop window change is still in progress by the user
     * @param animate if to animate the change to the image matrix, or set it directly
     */
    private fun handleCropWindowChanged(inProgress: Boolean, animate: Boolean) {
        val width = width
        val height = height
        if (mBitmap != null && width > 0 && height > 0) {
            val cropRect = mCropOverlayView!!.cropWindowRect
            if (inProgress) {
                if (cropRect!!.left < 0 || cropRect.top < 0 || cropRect.right > width || cropRect.bottom > height) {
                    applyImageMatrix(width, height, false, false)
                }
            }
//            else if (mAutoZoomEnabled || mZoom > 1) {
//                var newZoom = 0f
//                // keep the cropping window covered area to 50%-65% of zoomed sub-area
//                if (mZoom < mMaxZoom && cropRect!!.width() < width * 0.5f && cropRect.height() < height * 0.5f) {
//                    newZoom = mMaxZoom.toFloat().coerceAtMost(
//                        (width / (cropRect.width() / mZoom / 0.64f)).coerceAtMost(height / (cropRect.height() / mZoom / 0.64f))
//                    )
//                }
//                if (mZoom > 1 && (cropRect!!.width() > width * 0.65f || cropRect.height() > height * 0.65f)) {
//                    newZoom = 1f.coerceAtLeast(
//                        (width / (cropRect.width() / mZoom / 0.51f)).coerceAtMost(height / (cropRect.height() / mZoom / 0.51f))
//                    )
//                }
//                if (!mAutoZoomEnabled) {
//                    newZoom = 1f
//                }
//                if (newZoom > 0 && newZoom != mZoom) {
//                    if (animate) {
//                        if (mAnimation == null) {
//                            // lazy create animation single instance
//                            mAnimation = ImageAnimation(mImageView, mCropOverlayView)
//                        }
//                        // set the state for animation to start from
//                        mAnimation!!.setStartState(mImagePoints, mImageMatrix)
//                    }
//                    mZoom = newZoom
//                    applyImageMatrix(width, height, true, animate)
//                }
//            }
            if (mOnSetCropWindowChangeListener != null && !inProgress) {
                mOnSetCropWindowChangeListener!!.onCropWindowChanged()
            }
        }
    }

    /**
     * Apply matrix to handle the image inside the image view.
     *
     * @param width the width of the image view
     * @param height the height of the image view
     */
    private fun applyImageMatrix(width: Int, height: Int, center: Boolean, animate: Boolean) {
        Log.d("TAG", "applyImageMatrix: bitmap check::$width")
        Log.d("TAG", "applyImageMatrix: bitmap check::$height")

        if (mBitmap != null && width > 0 && height > 0) {

            try {
                mImageMatrix.invert(mImageInverseMatrix)
                val cropRect = mCropOverlayView!!.cropWindowRect
                mImageInverseMatrix.mapRect(cropRect)
                mImageMatrix.reset()

                // move the image to the center of the image view first so we can manipulate it from there
                mImageMatrix.postTranslate(
                    ((width - mBitmap!!.width) / 2).toFloat(),
                    ((height - mBitmap!!.height) / 2).toFloat()
                )
                mapImagePointsByImageMatrix()

                Log.d("TAG", "applyImageMatrix: check the data::$mDegreesRotated")

                // rotate the image the required degrees from center of image
                if (mDegreesRotated > 0) {
                    mImageMatrix.postRotate(
                        mDegreesRotated.toFloat(),
                        BitmapUtils.getRectCenterX(mImagePoints),
                        BitmapUtils.getRectCenterY(mImagePoints)
                    )
                    mapImagePointsByImageMatrix()
                }

                // scale the image to the image view, image rect transformed to know new width/height
                val scale = (width / BitmapUtils.getRectWidth(mImagePoints)).coerceAtMost(
                    height / BitmapUtils.getRectHeight(mImagePoints)
                )

                Log.d("TAG", "applyImageMatrix: check the data::$scale")

                if (mScaleType == ScaleType.FIT_CENTER || mScaleType == ScaleType.CENTER_INSIDE && scale < 1
                    || scale > 1 && mAutoZoomEnabled
                ) {
                    mImageMatrix.postScale(
                        scale,
                        scale,
                        BitmapUtils.getRectCenterX(mImagePoints),
                        BitmapUtils.getRectCenterY(mImagePoints)
                    )
                    mapImagePointsByImageMatrix()
                }

                // scale by the current zoom level
                val scaleX = if (mFlipHorizontally) -mZoom else mZoom
                val scaleY = if (mFlipVertically) -mZoom else mZoom

                Log.d("TAG", "applyImageMatrix: check the data::X:$scaleX")
                Log.d("TAG", "applyImageMatrix: check the data::Y:$scaleY")

                mImageMatrix.postScale(
                    scaleX,
                    scaleY,
                    BitmapUtils.getRectCenterX(mImagePoints),
                    BitmapUtils.getRectCenterY(mImagePoints)
                )


                mapImagePointsByImageMatrix()
                mImageMatrix.mapRect(cropRect)
                if (center) {
                    Log.d("TAG", "applyImageMatrix: check the data::center:$center")

                    // set the zoomed area to be as to the center of cropping window as possible
                    mZoomOffsetX =
                        if (width > BitmapUtils.getRectWidth(mImagePoints)) 0F else (width / 2 - cropRect!!.centerX()).coerceAtMost(
                            -BitmapUtils.getRectLeft(mImagePoints)
                        )
                            .coerceAtLeast(getWidth() - BitmapUtils.getRectRight(mImagePoints)) / scaleX

                    mZoomOffsetY =
                        if (height > BitmapUtils.getRectHeight(mImagePoints)) 0F else (height / 2 - cropRect!!.centerY()).coerceAtMost(
                            -BitmapUtils.getRectTop(
                                mImagePoints
                            )
                        )
                            .coerceAtLeast(getHeight() - BitmapUtils.getRectBottom(mImagePoints)) / scaleY

                    Log.d("TAG", "applyImageMatrix: check the data::mZoomOffsetX:$mZoomOffsetX")
                    Log.d("TAG", "applyImageMatrix: check the data::mZoomOffsetY:$mZoomOffsetY")


                } else {
                    // adjust the zoomed area so the crop window rectangle will be inside the area in case it
                    // was moved outside
                    mZoomOffsetX = ((mZoomOffsetX * scaleX).coerceAtLeast(-cropRect!!.left)
                        .coerceAtMost(-cropRect.right + width)
                            / scaleX)
                    mZoomOffsetY = ((mZoomOffsetY * scaleY).coerceAtLeast(-cropRect.top)
                        .coerceAtMost(-cropRect.bottom + height)
                            / scaleY)

                    Log.d(
                        "TAG",
                        "applyImageMatrix: check the data::mZoomOffsetX:else:$mZoomOffsetX"
                    )
                    Log.d(
                        "TAG",
                        "applyImageMatrix: check the data::mZoomOffsetY:else:$mZoomOffsetY"
                    )
                }

                // apply to zoom offset translate and update the crop rectangle to offset correctly
                mImageMatrix.postTranslate(mZoomOffsetX * scaleX, mZoomOffsetY * scaleY)
                cropRect!!.offset(mZoomOffsetX * scaleX, mZoomOffsetY * scaleY)
                mCropOverlayView.cropWindowRect = cropRect
                mapImagePointsByImageMatrix()
                mCropOverlayView.invalidate()

                // set matrix to apply
                if (animate) {
                    Log.d("TAG", "applyImageMatrix: check the data::animate:else:$animate")

                    // set the state for animation to end in, start animation now
                    mAnimation!!.setEndState(mImagePoints, mImageMatrix)
                    mImageView.startAnimation(mAnimation)
                } else {
                    mImageView.imageMatrix = mImageMatrix
                    Log.d("TAG", "applyImageMatrix: check the data::animate:else:$animate")

                }

                // update the image rectangle in the crop overlay
                updateImageBounds(false)
            } catch (E: Exception) {
                Log.d("TAG", "applyImageMatrix: :::${E.message}")
            }
        }
    }

    /**
     * Adjust the given image rectangle by image transformation matrix to know the final rectangle of
     * the image.<br></br>
     * To get the proper rectangle it must be first reset to original image rectangle.
     */
//    private fun mapImagePointsByImageMatrix() {
//        mImagePoints[0] = 0f
//        mImagePoints[1] = 0f
//        mImagePoints[2] = 1000f
//        mImagePoints[3] = 0f
//        mImagePoints[4] = 1000f
//        mImagePoints[5] = 1000f
//        mImagePoints[6] = 0f
//        mImagePoints[7] = 1000f
//        mImageMatrix.mapPoints(mImagePoints)
//        mScaleImagePoints[0] = 0f
//        mScaleImagePoints[1] = 0f
//        mScaleImagePoints[2] = 1000f
//        mScaleImagePoints[3] = 0f
//        mScaleImagePoints[4] = 1000f
//        mScaleImagePoints[5] = 1000f
//        mScaleImagePoints[6] = 0f
//        mScaleImagePoints[7] = 1000f
//        mImageMatrix.mapPoints(mScaleImagePoints)
//    }


    private fun mapImagePointsByImageMatrix() {
        mImagePoints[0] = 0f
        mImagePoints[1] = 0f
        mImagePoints[2] = mBitmap!!.width.toFloat()
        mImagePoints[3] = 0f
        mImagePoints[4] = mBitmap!!.width.toFloat()
        mImagePoints[5] = mBitmap!!.height.toFloat()
        mImagePoints[6] = 0f
        mImagePoints[7] = mBitmap!!.height.toFloat()
        mImageMatrix.mapPoints(mImagePoints)
        mScaleImagePoints[0] = 0f
        mScaleImagePoints[1] = 0f
        mScaleImagePoints[2] = 100f
        mScaleImagePoints[3] = 0f
        mScaleImagePoints[4] = 100f
        mScaleImagePoints[5] = 100f
        mScaleImagePoints[6] = 0f
        mScaleImagePoints[7] = 100f
        mImageMatrix.mapPoints(mScaleImagePoints)
    }

    /**
     * Set visibility of crop overlay to hide it when there is no image or specificly set by client.
     */
    private fun setCropOverlayVisibility() {
        if (mCropOverlayView != null) {
            mCropOverlayView.visibility =
                if (mShowCropOverlay && mBitmap != null) VISIBLE else INVISIBLE
        }
    }

    fun todrawOvalBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawOval(rect, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        bitmap.recycle()
        return output


    }


    fun todrawTriangle(): Bitmap {
        val width = mBitmap!!.width
        val height = mBitmap!!.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        paint.style = Paint.Style.FILL
        val path = Path()
        val halfWidth = width / 2
        val x = width / 2f
        val y = height / 100f * 50.5f
        path.moveTo(x, y - halfWidth) // Top
        path.lineTo(x - halfWidth, y + halfWidth) // Bottom left
        path.lineTo(x + halfWidth, y + halfWidth) // Bottom right
        path.lineTo(x, y - halfWidth) // Back to Top
        path.close()
        canvas.drawPath(path, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(mBitmap!!, 0f, 0f, paint)
        mBitmap!!.recycle()
        return output
    }


    /**
     * Set visibility of progress bar when async loading/cropping is in process and show is enabled.
     */
    public fun progressbarset() {
        mHandler.post(Runnable {
            // Update your UI

            val visible = (mShowProgressBar
                    && (mBitmap == null && mBitmapLoading != null
                    || mBitmapCropping != null))

            mProgressBar.visibility = if (visible) VISIBLE else INVISIBLE
            Log.d("TAG", "setProgressBarVisibility: check the visibility:$visible")

        })
    }

    /** Update the scale factor between the actual image bitmap and the shown image.<br></br>  */
    private fun updateImageBounds(clear: Boolean) {
        if (mBitmap != null && !clear) {

            // Get the scale factor between the actual Bitmap dimensions and the displayed dimensions for
            // width/height.
            val scaleFactorWidth =
                100f * mLoadedSampleSize / BitmapUtils.getRectWidth(mScaleImagePoints)
            val scaleFactorHeight =
                100f * mLoadedSampleSize / BitmapUtils.getRectHeight(mScaleImagePoints)
            mCropOverlayView!!.setCropWindowLimits(
                width.toFloat(), height.toFloat(), scaleFactorWidth, scaleFactorHeight
            )
        }

        val points = floatArrayOf(
            31.95f, 12.418856f,
            20.63289f, 11.223692f,
            16f, 0.83228856f,
            11.367113f, 11.223692f,
            0.05000003f, 12.418856f,
            8.503064f, 20.03748f,
            6.1431603f, 31.167711f,
            16f, 25.48308f,
            25.85684f, 31.167711f,
            23.496937f, 20.03748f
        )

        // set the bitmap rectangle and update the crop window after scale factor is set
        mCropOverlayView!!.setBounds(if (clear) null else mImagePoints, width, height)
        Log.d("TAG", "setBounds: check the data::::687")

    }
    // endregion
    // region: Inner class: CropShape
    /**
     * The possible cropping area shape.<br></br>
     * To set square/circle crop shape set aspect ratio to 1:1.
     */
    enum class CropShape {
        RECTANGLE, OVAL, ROUNDRECT, HEXAGONE, STAR, TRIANGLE, check


    }
    // endregion
    // region: Inner class: ScaleType
    /**
     * Options for scaling the bounds of cropping image to the bounds of Crop Image View.<br></br>
     * Note: Some options are affected by auto-zoom, if enabled.
     */
    enum class ScaleType {
        FIT_CENTER,
        CENTER,
        CENTER_CROP,
        CENTER_INSIDE
    }
    // endregion
    // region: Inner class: Guidelines
    /** The possible guidelines showing types.  */
    enum class Guidelines {
        OFF,
        ON_TOUCH,
        ON
    }
    // endregion
    // region: Inner class: RequestSizeOptions
    /** Possible options for handling requested width/height for cropping.  */
    enum class RequestSizeOptions {
        NONE,
        SAMPLING,
        RESIZE_INSIDE,
        RESIZE_FIT,
        RESIZE_EXACT
    }
    // endregion
    // region: Inner class: OnSetImageUriCompleteListener
    /** Interface definition for a callback to be invoked when the crop overlay is released.  */
    interface OnSetCropOverlayReleasedListener {
        /**
         * Called when the crop overlay changed listener is called and inProgress is false.
         *
         * @param rect The rect coordinates of the cropped overlay
         */
        fun onCropOverlayReleased(rect: Rect?)
    }

    /** Interface definition for a callback to be invoked when the crop overlay is released.  */
    interface OnSetCropOverlayMovedListener {
        fun onCropOverlayMoved(rect: Rect?)
    }

    /** Interface definition for a callback to be invoked when the crop overlay is released.  */
    interface OnSetCropWindowChangeListener {
        fun onCropWindowChanged()
    }

    /** Interface definition for a callback to be invoked when image async loading is complete.  */
    interface OnSetImageUriCompleteListener {
        fun onNavigationItemSelected(item: MenuItem): Boolean

        fun onSetImageUriComplete(view: CropImageView?, uri: Uri?, error: Exception?)
    }
    // endregion
    // region: Inner class: OnGetCroppedImageCompleteListener
    /** Interface definition for a callback to be invoked when image async crop is complete.  */
    interface OnCropImageCompleteListener {
        fun onCropImageComplete(view: CropImageView?, result: CropResult?)
    }
    // endregion
    // region: Inner class: ActivityResult
    /** Result data of crop image.  */
    open class CropResult
    internal constructor(

        val originalBitmap: Bitmap?,
        val originalUri: Uri?,
        val bitmap: Bitmap?,
        val uri: Uri?,
        val error: Exception?,
        val cropPoints: FloatArray,
        val cropRect: Rect?,
        val wholeImageRect: Rect?,
        val rotation: Int,
        val sampleSize: Int,
    ) {

        val isSuccessful: Boolean
            get() = error == null
    } // endregion

    companion object {

        private fun getOnMeasureSpec(
            measureSpecMode: Int,
            measureSpecSize: Int,
            desiredSize: Int,
        ): Int {

            // Measure Width
            val spec: Int = if (measureSpecMode == MeasureSpec.EXACTLY) {
                // Must be this size
                measureSpecSize
            } else if (measureSpecMode == MeasureSpec.AT_MOST) {
                // Can't be bigger than...; match_parent value
                Math.min(desiredSize, measureSpecSize)
            } else {
                // Be whatever you want; wrap_content
                desiredSize
            }
            return spec
        }
    }


}