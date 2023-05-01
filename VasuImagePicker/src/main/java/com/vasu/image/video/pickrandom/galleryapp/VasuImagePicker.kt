package com.vasu.image.video.pickrandom.galleryapp

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import com.vasu.image.video.pickrandom.galleryapp.activity.ImagePickerActivity
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.RC_PICK_IMAGES
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.backIcon
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.backgroundColor
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.buttonColor
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.cropIconInCrop
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.customCropDrawable
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.doneIcon
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.doneTitle
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.flipHorizontal
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.flipVertical
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.folderCountColor
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.folderTitle
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.folderTitleColor
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.freeDrawable
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.imageCount
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.imageTitle
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.isAlwaysShowDoneButton
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.isCrop
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.isFolderMode
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.isKeepScreenOn
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.isMultipleMode
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.limitMessage
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.mCropIconListt
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.maxSize
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.mediaType
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.progressBarColor
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.ratioX
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.ratioY
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.requestCode
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.rotateIconInCrop
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.rotateLeftIconInCrop
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.rotateRightIconInCrop
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.savePath
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.statusBarColor
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.toolbarColor
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.toolbarIconColor
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.toolbarTextColor
import com.vasu.image.video.pickrandom.galleryapp.model.CropModel
import com.vasu.image.video.pickrandom.galleryapp.model.SavePath

open class VasuImagePicker {
//    private var config: Config? = null

    public class ActivityBuilder(private val activity: Activity) :
        VasuImagePicker.Builder() {
        override fun start() {
            val intent = intent
            val requestCode =
                if (requestCode != 0) requestCode else RC_PICK_IMAGES
            if (true) {
                activity.startActivityForResult(intent, requestCode)
            } else {
                activity.overridePendingTransition(0, 0)
                activity.startActivityForResult(intent, requestCode)
            }
        }

        override val intent: Intent
            get() {
                val intent: Intent
                intent = Intent(activity, ImagePickerActivity::class.java)
//                intent.putExtra(Config.EXTRA_CONFIG, config)
                return intent
            }

    }

    abstract class Builder  {
//        constructor(activity: Activity?) : super(activity) {}
//        constructor(fragment: Fragment) : super(fragment.context) {}

        fun setToolbarColor(mtoolbarColor: String?): Builder {
            toolbarColor = mtoolbarColor
//            config.setToolbarColor(toolbarColor)
            return this
        }

        fun setStatusBarColor(mstatusBarColor: String?): Builder {
            statusBarColor = mstatusBarColor
//            config.setStatusBarColor(statusBarColor)
            return this
        }

        fun setToolbarTextColor(mtoolbarTextColor: String?): Builder {
            toolbarTextColor = mtoolbarTextColor
//            config.setToolbarTextColor(toolbarTextColor)
            return this
        }

        fun setToolbarIconColor(mtoolbarIconColor: String?): Builder {
            toolbarIconColor = mtoolbarIconColor
//            config.setToolbarIconColor(toolbarIconColor)
            return this
        }
        fun setBackIcon(mbackIcon: Drawable?): Builder {
            backIcon= mbackIcon
//            config.setToolbarIconColor(toolbarIconColor)
            return this
        }

        fun setDoneIcon(mDoneIcon: Drawable?): Builder {
            doneIcon= mDoneIcon
//            config.setToolbarIconColor(toolbarIconColor)
            return this
        }

        fun setCustomCropIcon(mcustomCrop : Drawable?):Builder{
            customCropDrawable  = mcustomCrop
            return this
        }

        fun setCustomCropIconInCrop(mcustomCrop : Drawable?):Builder{
            cropIconInCrop  = mcustomCrop
            return this
        }
  fun setCustomRotateIconInCrop(mcustomCrop : Drawable?):Builder{
            rotateIconInCrop  = mcustomCrop
            return this
        }
fun setCustomRotateLeftIconInCrop(mcustomCrop : Drawable?):Builder{
            rotateLeftIconInCrop  = mcustomCrop
            return this
        }

        fun setCustomRotateRightIconInCrop(mcustomCrop : Drawable?):Builder{
            rotateRightIconInCrop  = mcustomCrop
            return this
        }
        fun setFlipHorizontalInCrop(mcustomCrop: Drawable?): Builder {
            flipHorizontal = mcustomCrop
            return this
        }

        fun setFlipVerticalInCrop(mcustomCrop: Drawable?): Builder {
            flipVertical = mcustomCrop
            return this
        }

        fun setProgressBarColor(mprogressBarColor: String?): Builder {
            progressBarColor = mprogressBarColor
//            config.setProgressBarColor(progressBarColor)
            return this
        }
        fun setCropIconListt(CropIconListt: ArrayList<CropModel>?): Builder {
            mCropIconListt = CropIconListt
//            config.setProgressBarColor(progressBarColor)
            return this
        }

        fun setFolderTitleColor(CropIconListt: String?): Builder {
            folderTitleColor = CropIconListt
//            config.setProgressBarColor(progressBarColor)
            return this
        }

        fun setFolderCountColor(CropIconListt: String?): Builder {
            folderCountColor = CropIconListt
//            config.setProgressBarColor(progressBarColor)
            return this
        }

        fun setFreeIconInCrop(mFreeIcon : Drawable?){
            freeDrawable = mFreeIcon
        }

//        fun setCropRatioDrawables(list : List<Drawable>){
//            cropRatioList = list
//        }

        fun setBackgroundColor(mbackgroundColor: String?): Builder {
            backgroundColor = mbackgroundColor
//            config.setBackgroundColor(backgroundColor)
            return this
        }

        fun setMultipleMode(misMultipleMode: Boolean): Builder {
            isMultipleMode = misMultipleMode
//            config.isMultipleMode = isMultipleMode
            return this
        }

        fun setImageCount(count: Int): Builder {
            imageCount = count
//            config.imageCount = count
            return this
        }

        fun setFolderMode(misFolderMode: Boolean): Builder {
            isFolderMode = misFolderMode
//            config.isFolderMode = true
            return this
        }

        fun setMaxSize(mmaxSize: Int): Builder {
            maxSize = mmaxSize
//            config.maxSize = maxSize
            return this
        }

        fun setDoneTitle(mdoneTitle: String?): Builder {
            doneTitle = mdoneTitle
//            config.doneTitle = doneTitle
            return this
        }

        fun setFolderTitle(mfolderTitle: String?): Builder {
            folderTitle = mfolderTitle
//            config.folderTitle = folderTitle
            return this
        }

        fun setImageTitle(mimageTitle: String?): Builder {
            imageTitle = mimageTitle
//            config.imageTitle = imageTitle
            return this
        }

        fun setLimitMessage(message: String?): Builder {
            limitMessage = message
//            config.limitMessage = message
            return this
        }

        fun setSavePath(path: String?): Builder {
            savePath = SavePath(path , false)
//            config.savePath = SavePath(path, false)
            return this
        }

        fun setAlwaysShowDoneButton(misAlwaysShowDoneButton: Boolean): Builder {
            isAlwaysShowDoneButton = misAlwaysShowDoneButton
//            config.isAlwaysShowDoneButton = isAlwaysShowDoneButton
            return this
        }

        fun setKeepScreenOn(keepScreenOn: Boolean): Builder {
//            config.isKeepScreenOn = keepScreenOn
            isKeepScreenOn = keepScreenOn
            return this
        }

        fun setSelectedRatio(x: Int, y: Int): Builder {
            ratioX = x;
            ratioY = y;
//            config.ratioX = x;
//            config.ratioY = y;
            return this
        }

        fun setIsCrop(misCrop : Boolean) : Builder{
//            config.isCrop = isCrop
            isCrop = misCrop

            return this
        }

        fun setMediaType(type : String) : Builder{
//            config.mediaType = type
            mediaType = type
            return this
        }
//        fun setSelectedImages(selectedImages: ArrayList<Image?>?): Builder {
//            config.setSelectedImages(selectedImages)
//            return this
//        }

        fun setButtonColor(color:String) : Builder{
//            config.setButtonColor(color)
            buttonColor = color
            return this
        }

        fun setRequestCode(mrequestCode: Int): Builder {
//            config.requestCode = requestCode
            requestCode = mrequestCode
            return this
        }

        abstract fun start()


        abstract val intent: Intent?
    }

//    abstract class BaseBuilder(context: Context?) {
//        var config: Config = Config()
//
//        init {
//            val resources = context!!.resources
//            config.isMultipleMode = true
//            config.isFolderMode = true
//            config.maxSize = Config.MAX_SIZE
//            config.doneTitle = resources.getString(R.string.imagepicker_action_done)
//            config.folderTitle = resources.getString(R.string.imagepicker_title_folder)
//            config.imageTitle = resources.getString(R.string.imagepicker_title_image)
//            config.limitMessage = resources.getString(R.string.imagepicker_msg_limit_images)
//            config.setSavePath(SavePath.DEFAULT)
//            config.isAlwaysShowDoneButton = false
//            config.isKeepScreenOn = false
//            config.isCrop = false
//            config.mediaType = Constant.BOTH
////            config.setSelectedImages(ArrayList<Image>())
//        }
//    }
}