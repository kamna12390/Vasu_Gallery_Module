package com.vasu.image.video.pickrandom.galleryapp.Crop

import android.content.res.Resources
import android.os.Parcelable
import android.graphics.Bitmap.CompressFormat
import android.util.TypedValue
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Parcel
import android.text.TextUtils
import com.example.imagecrop.Crop.CropImageView

class ImageOptions : Parcelable {
    @JvmField
    var cropShape: CropImageView.CropShape
    @JvmField
    var snapRadius: Float
    @JvmField
    var touchRadius: Float
    @JvmField
    var guidelines: CropImageView.Guidelines
    var scaleType: CropImageView.ScaleType
    var showCropOverlay: Boolean
    var showProgressBar: Boolean
    var autoZoomEnabled: Boolean
    @JvmField
    var multiTouchEnabled: Boolean
    var maxZoom: Int
    @JvmField
    var initialCropWindowPaddingRatio: Float
    @JvmField
    var fixAspectRatio: Boolean
    @JvmField
    var aspectRatioX: Int
    @JvmField
    var aspectRatioY: Int
    @JvmField
    var borderLineThickness: Float
    @JvmField
    var borderLineColor: Int

    @JvmField
    var borderCornerThickness: Float
    @JvmField
    var borderCornerOffset: Float
    @JvmField
    var borderCornerLength: Float
    @JvmField
    var borderCornerColor: Int
    @JvmField
    var guidelinesThickness: Float
    @JvmField
    var guidelinesColor: Int


    @JvmField
    var backgroundColor: Int

    var minCropWindowWidth: Int
    var minCropWindowHeight: Int
    var minCropResultWidth: Int
    var minCropResultHeight: Int
    var maxCropResultWidth: Int
    var maxCropResultHeight: Int
    var activityTitle: CharSequence
    var activityMenuIconColor: Int
    var outputUri: Uri?
    var outputCompressFormat: CompressFormat
    var outputCompressQuality: Int
    var outputRequestWidth: Int
    var outputRequestHeight: Int
    var outputRequestSizeOptions: CropImageView.RequestSizeOptions
     var noOutputImage: Boolean
    var initialCropWindowRectangle: Rect?
    var initialRotation: Int
    var allowRotation: Boolean
    var allowFlipping: Boolean
    var allowCounterRotation: Boolean
    var rotationDegrees: Int
    var flipHorizontally: Boolean
    var flipVertically: Boolean
    var cropMenuCropButtonTitle: CharSequence?
    var cropMenuCropButtonIcon: Int

    constructor() {
        val dm = Resources.getSystem().displayMetrics
        cropShape = CropImageView.CropShape.RECTANGLE
        snapRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, dm)
        touchRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, dm)
        guidelines = CropImageView.Guidelines.ON_TOUCH
        scaleType = CropImageView.ScaleType.FIT_CENTER
        showCropOverlay = true
        showProgressBar = true
        autoZoomEnabled = true
        multiTouchEnabled = false
        maxZoom = 4
        initialCropWindowPaddingRatio = 0.1f
        fixAspectRatio = false
        aspectRatioX = 1
        aspectRatioY = 1
        borderLineThickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, dm)
        borderLineColor = Color.argb(170, 255, 255, 255)
        borderCornerThickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, dm)
        borderCornerOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, dm)
        borderCornerLength = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14f, dm)
        borderCornerColor = Color.WHITE
        guidelinesThickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, dm)
        guidelinesColor = Color.argb(170, 255, 255, 255)
        backgroundColor = Color.argb(119, 0, 0, 0)
        minCropWindowWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, dm).toInt()
        minCropWindowHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, dm).toInt()
        minCropResultWidth = 40
        minCropResultHeight = 40
        maxCropResultWidth = 99999
        maxCropResultHeight = 99999
        activityTitle = ""
        activityMenuIconColor = 0
        outputUri = Uri.EMPTY
        outputCompressFormat = CompressFormat.JPEG
        outputCompressQuality = 90
        outputRequestWidth = 0
        outputRequestHeight = 0
        outputRequestSizeOptions = CropImageView.RequestSizeOptions.NONE
        noOutputImage = false
        initialCropWindowRectangle = null
        initialRotation = -1
        allowRotation = true
        allowFlipping = true
        allowCounterRotation = false
        rotationDegrees = 90
        flipHorizontally = false
        flipVertically = false
        cropMenuCropButtonTitle = null
        cropMenuCropButtonIcon = 0
    }

    /** Create object from parcel.  */
     constructor(parcel: Parcel) {
        cropShape = CropImageView.CropShape.values()[parcel.readInt()]
        snapRadius = parcel.readFloat()
        touchRadius = parcel.readFloat()
        guidelines = CropImageView.Guidelines.values()[parcel.readInt()]
        scaleType = CropImageView.ScaleType.values()[parcel.readInt()]
        showCropOverlay = parcel.readByte().toInt() != 0
        showProgressBar = parcel.readByte().toInt() != 0
        autoZoomEnabled = parcel.readByte().toInt() != 0
        multiTouchEnabled = parcel.readByte().toInt() != 0
        maxZoom = parcel.readInt()
        initialCropWindowPaddingRatio = parcel.readFloat()
        fixAspectRatio = parcel.readByte().toInt() != 0
        aspectRatioX = parcel.readInt()
        aspectRatioY = parcel.readInt()
        borderLineThickness = parcel.readFloat()
        borderLineColor = parcel.readInt()
        borderCornerThickness = parcel.readFloat()
        borderCornerOffset = parcel.readFloat()
        borderCornerLength = parcel.readFloat()
        borderCornerColor = parcel.readInt()
        guidelinesThickness = parcel.readFloat()
        guidelinesColor = parcel.readInt()
        backgroundColor = parcel.readInt()
        minCropWindowWidth = parcel.readInt()
        minCropWindowHeight = parcel.readInt()
        minCropResultWidth = parcel.readInt()
        minCropResultHeight = parcel.readInt()
        maxCropResultWidth = parcel.readInt()
        maxCropResultHeight = parcel.readInt()
        activityTitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel)
        activityMenuIconColor = parcel.readInt()
        outputUri = parcel.readParcelable(Uri::class.java.classLoader)
        outputCompressFormat = CompressFormat.valueOf(parcel.readString()!!)
        outputCompressQuality = parcel.readInt()
        outputRequestWidth = parcel.readInt()
        outputRequestHeight = parcel.readInt()
        outputRequestSizeOptions = CropImageView.RequestSizeOptions.values()[parcel.readInt()]
        noOutputImage = parcel.readByte().toInt() != 0
        initialCropWindowRectangle = parcel.readParcelable(Rect::class.java.classLoader)
        initialRotation = parcel.readInt()
        allowRotation = parcel.readByte().toInt() != 0
        allowFlipping = parcel.readByte().toInt() != 0
        allowCounterRotation = parcel.readByte().toInt() != 0
        rotationDegrees = parcel.readInt()
        flipHorizontally = parcel.readByte().toInt() != 0
        flipVertically = parcel.readByte().toInt() != 0
        cropMenuCropButtonTitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel)
        cropMenuCropButtonIcon = parcel.readInt()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(cropShape.ordinal)
        dest.writeFloat(snapRadius)
        dest.writeFloat(touchRadius)
        dest.writeInt(guidelines.ordinal)
        dest.writeInt(scaleType.ordinal)
        dest.writeByte((if (showCropOverlay) 1 else 0).toByte())
        dest.writeByte((if (showProgressBar) 1 else 0).toByte())
        dest.writeByte((if (autoZoomEnabled) 1 else 0).toByte())
        dest.writeByte((if (multiTouchEnabled) 1 else 0).toByte())
        dest.writeInt(maxZoom)
        dest.writeFloat(initialCropWindowPaddingRatio)
        dest.writeByte((if (fixAspectRatio) 1 else 0).toByte())
        dest.writeInt(aspectRatioX)
        dest.writeInt(aspectRatioY)
        dest.writeFloat(borderLineThickness)
        dest.writeInt(borderLineColor)
        dest.writeFloat(borderCornerThickness)
        dest.writeFloat(borderCornerOffset)
        dest.writeFloat(borderCornerLength)
        dest.writeInt(borderCornerColor)
        dest.writeFloat(guidelinesThickness)
        dest.writeInt(guidelinesColor)
        dest.writeInt(backgroundColor)
        dest.writeInt(minCropWindowWidth)
        dest.writeInt(minCropWindowHeight)
        dest.writeInt(minCropResultWidth)
        dest.writeInt(minCropResultHeight)
        dest.writeInt(maxCropResultWidth)
        dest.writeInt(maxCropResultHeight)
        TextUtils.writeToParcel(activityTitle, dest, flags)
        dest.writeInt(activityMenuIconColor)
        dest.writeParcelable(outputUri, flags)
        dest.writeString(outputCompressFormat.name)
        dest.writeInt(outputCompressQuality)
        dest.writeInt(outputRequestWidth)
        dest.writeInt(outputRequestHeight)
        dest.writeInt(outputRequestSizeOptions.ordinal)
        dest.writeInt(if (noOutputImage) 1 else 0)
        dest.writeParcelable(initialCropWindowRectangle, flags)
        dest.writeInt(initialRotation)
        dest.writeByte((if (allowRotation) 1 else 0).toByte())
        dest.writeByte((if (allowFlipping) 1 else 0).toByte())
        dest.writeByte((if (allowCounterRotation) 1 else 0).toByte())
        dest.writeInt(rotationDegrees)
        dest.writeByte((if (flipHorizontally) 1 else 0).toByte())
        dest.writeByte((if (flipVertically) 1 else 0).toByte())
        TextUtils.writeToParcel(cropMenuCropButtonTitle, dest, flags)
        dest.writeInt(cropMenuCropButtonIcon)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun validate() {
        require(maxZoom >= 0) { "Cannot set max zoom to a number < 1" }
        require(touchRadius >= 0) { "Cannot set touch radius value to a number <= 0 " }
        require(!(initialCropWindowPaddingRatio < 0 || initialCropWindowPaddingRatio >= 0.5)) { "Cannot set initial crop window padding value to a number < 0 or >= 0.5" }
        require(aspectRatioX > 0) { "Cannot set aspect ratio value to a number less than or equal to 0." }
        require(aspectRatioY > 0) { "Cannot set aspect ratio value to a number less than or equal to 0." }
        require(borderLineThickness >= 0) { "Cannot set line thickness value to a number less than 0." }
        require(borderCornerThickness >= 0) { "Cannot set corner thickness value to a number less than 0." }
        require(guidelinesThickness >= 0) { "Cannot set guidelines thickness value to a number less than 0." }
        require(minCropWindowHeight >= 0) { "Cannot set min crop window height value to a number < 0 " }
        require(minCropResultWidth >= 0) { "Cannot set min crop result width value to a number < 0 " }
        require(minCropResultHeight >= 0) { "Cannot set min crop result height value to a number < 0 " }
        require(maxCropResultWidth >= minCropResultWidth) { "Cannot set max crop result width to smaller value than min crop result width" }
        require(maxCropResultHeight >= minCropResultHeight) { "Cannot set max crop result height to smaller value than min crop result height" }
        require(outputRequestWidth >= 0) { "Cannot set request width value to a number < 0 " }
        require(outputRequestHeight >= 0) { "Cannot set request height value to a number < 0 " }
        require(!(rotationDegrees < 0 || rotationDegrees > 360)) { "Cannot set rotation degrees value to a number < 0 or > 360" }
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ImageOptions> =
            object : Parcelable.Creator<ImageOptions> {
                override fun createFromParcel(parcel: Parcel): ImageOptions {
                    return ImageOptions(parcel)
                }

                override fun newArray(size: Int): Array<ImageOptions?> {
                    return arrayOfNulls(size)
                }
            }
    }
}