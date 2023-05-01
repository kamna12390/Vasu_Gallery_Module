package com.vasu.image.video.pickrandom.galleryapp.helper

import android.graphics.drawable.Drawable
import android.net.Uri
import com.vasu.image.video.pickrandom.galleryapp.model.CropModel
import com.vasu.image.video.pickrandom.galleryapp.model.SavePath

object Constant {

    var lastSelectedURI: Uri? = null
    var lastSelectedPosition: Int = -1
    var selectedImage: List<Uri> = ArrayList<Uri>()
    val IMAGE = "image"
    val VIDEO = "video"
    val BOTH = "both"


    var toolbarColor: String? = null
    var statusBarColor: String? = null
    var toolbarTextColor: String? = null

    var mediaType: String? = BOTH
    var toolbarIconColor: String? = null
    var buttonColor: String? = null
    var progressBarColor: String? = null
    var mCropIconListt: ArrayList<CropModel>?=null
    var imageCount = 0
    var backgroundColor: String? = null
    var isMultipleMode = false
    var isFolderMode = false
    var maxSize = 0
    var doneTitle: String? = null
    var folderTitle: String? = null
    var folderTitleColor: String? = null
    var folderCountColor: String? = null
    var ratioX = 0
    var ratioY = 0
    var imageTitle: String? = null
    var limitMessage: String? = null
    var savePath: SavePath? = null
    var isAlwaysShowDoneButton = false
    var isKeepScreenOn = false
    var requestCode = 0
    var backIcon : Drawable? = null
    var doneIcon : Drawable? = null
    var isCrop = false
    val RC_PICK_IMAGES = 100
    val RC_PICK_STICKER = 1434
    val RC_CAPTURE_IMAGE = 101;
    val RC_WRITE_EXTERNAL_STORAGE_PERMISSION = 102;
    val RC_CAMERA_PERMISSION = 103;


    var freeDrawable : Drawable? = null
    var customCropDrawable : Drawable? = null
    var cropIconInCrop : Drawable? = null
    var rotateIconInCrop : Drawable? = null
    var rotateLeftIconInCrop : Drawable? = null
    var rotateRightIconInCrop : Drawable? = null
    var flipHorizontal : Drawable? = null
    var flipVertical : Drawable? = null
//    var cropRatioList: List<Drawable?> = listOf(*arrayOfNulls(11))

}