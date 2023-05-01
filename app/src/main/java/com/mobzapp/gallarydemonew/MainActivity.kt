package com.mobzapp.gallarydemonew

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.imagecrop.Crop.CropImageView
import com.mobzapp.gallarydemonew.databinding.ActivityMainBinding
import com.vasu.image.video.pickrandom.galleryapp.VasuImagePicker
import com.vasu.image.video.pickrandom.galleryapp.activity.ImagePickerActivity
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant
import com.vasu.image.video.pickrandom.galleryapp.model.CropModel
//import com.vasu.image.video.pickrandom.galleryapp.model.Config
import com.vasu.image.video.pickrandom.galleryapp.util.CropImage
import java.io.IOException
import kotlin.math.log


class MainActivity : AppCompatActivity() {
    lateinit var bind: ActivityMainBinding
    private val RESULT_LOAD_GALLERY = 103
    private val RESULT_LOAD_GALLERY_CROP = 104
    private val RESULT_LOAD_IMAGE_ONLY = 105
    private val RESULT_LOAD_IMAGE__ONLY_NO_CROP = 106
    private val RESULT_LOAD_VIDEO_ONLY = 107
    private val RESULT_LOAD_GALLERY_MULTIPLE = 108
    private val RESULT_LOAD_IMAGE_MULTIPLE = 109
    private val RESULT_LOAD_VIDEO_MULTIPLE = 110
    private lateinit var uri: Uri

    private var gallaeryType = RESULT_LOAD_GALLERY
    private lateinit var adapterImage: ImageRecyclerAdapter
//    var config : Config? = null


    fun isClickButton(
        mIsCrop: Boolean,
        mMultipleMode: Boolean,
        mMediaType: String,
        mRequestCode: Int,
    ) {
        var mCropIconListt = arrayListOf(
            CropModel(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.ic_free
                ), "Free", 0, 0
            ), CropModel(
                AppCompatResources.getDrawable(this, R.drawable.ic_custom), "Custom", 0, 0
            ),
            CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_11)!!, "1:1", 1, 1),
            CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_23)!!, "2:3", 2, 3),
            CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_32)!!, "3:2", 3, 2),
            CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_34)!!, "3:4", 3, 4),
            CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_13)!!, "1:3", 1, 3),
            CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_43)!!, "4:3", 4, 3),
            CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_21)!!, "2:1", 2, 1),
            CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_26)!!, "2:6", 2, 6),
            CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_916)!!, "9:16", 9, 16),
            CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_45)!!, "4:5", 4, 5),
            CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_169)!!, "16:9", 16, 9)
        )
        with(VasuImagePicker.ActivityBuilder(this)) {
            setFolderMode(true)
            setFolderTitle("Gallery")
            setImageCount(1)
            setMaxSize(10)
            setBackgroundColor("#93A7AA")
            setToolbarColor("#2E5E4E")
            setToolbarTextColor("#DBD3D8")
            setToolbarIconColor("#DBD3D8")
            setStatusBarColor("#2E5E4E")
            setProgressBarColor("#2E5E4E")
            setFolderTitleColor("#A7B5B9")
            setFolderCountColor("#A7B5B9")
            setButtonColor("#02281C")
            setCropIconListt(mCropIconListt)
            setBackIcon(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.back
                )
            )
            setDoneIcon(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.done
                )
            )

            setCustomCropIconInCrop(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.crop
                )
            )
            setCustomRotateIconInCrop(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.crop_alt
                )
            )
            setCustomRotateLeftIconInCrop(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.gesture_rotate_left
                )
            )
            setCustomRotateRightIconInCrop(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.gesture_rotate_right
                )
            )
            setFlipHorizontalInCrop(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.flip_horizontal_icon
                )
            )
            setFlipVerticalInCrop(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.flip_vertical_icon
                )
            )
            setKeepScreenOn(true)
            setAlwaysShowDoneButton(true)
            setMultipleMode(mMultipleMode)
            setRequestCode(mRequestCode)
            setIsCrop(mIsCrop)
            setMediaType(mMediaType)
            start()

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        when (requestCode) {
            1001 -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        when (gallaeryType) {
                            RESULT_LOAD_GALLERY -> {
                                isClickButton(
                                    mIsCrop = false,
                                    mMultipleMode = false,
                                    mMediaType = Constant.BOTH,
                                    mRequestCode = RESULT_LOAD_GALLERY
                                )
                            }


                            RESULT_LOAD_GALLERY_CROP -> {

                                isClickButton(
                                    mIsCrop = true,
                                    mMultipleMode = false,
                                    mMediaType = Constant.BOTH,
                                    mRequestCode = RESULT_LOAD_GALLERY_CROP
                                )
//
//                                VasuImagePicker.ActivityBuilder(this)
//                                    .setFolderMode(true)
//                                    .setFolderTitle("Gallery")
//                                    .setMultipleMode(false)
//                                    .setImageCount(1)
//                                    .setMaxSize(10)
//                                    .setBackgroundColor("#232326")
//                                    .setToolbarColor("#303032")
//                                    .setToolbarTextColor("#FFFFFF")
//                                    .setToolbarIconColor("#FFFFFF")
//                                    .setStatusBarColor("#303032")
//                                    .setProgressBarColor("#35b18e")
//                                    .setButtonColor("#35b18e")
//                                    .setAlwaysShowDoneButton(true)
//                                    .setRequestCode(RESULT_LOAD_GALLERY_CROP)
//                                    .setKeepScreenOn(true)
//                                    .setIsCrop(true)
//                                    .start()
                            }


                            RESULT_LOAD_IMAGE_ONLY -> {

                                isClickButton(
                                    mIsCrop = true,
                                    mMultipleMode = false,
                                    mMediaType = Constant.IMAGE,
                                    mRequestCode = RESULT_LOAD_IMAGE_ONLY
                                )

                            }


                            RESULT_LOAD_IMAGE__ONLY_NO_CROP -> {

                                isClickButton(
                                    mIsCrop = false,
                                    mMultipleMode = false,
                                    mMediaType = Constant.IMAGE,
                                    mRequestCode = RESULT_LOAD_IMAGE__ONLY_NO_CROP
                                )
//                                 VasuImagePicker.ActivityBuilder(this)
//                                    .setFolderMode(true)
//                                    .setFolderTitle("Gallery")
//                                    .setMultipleMode(false)
//                                    .setImageCount(1)
//                                    .setMaxSize(10)
//                                     .setBackgroundColor("#232326")
//                                     .setToolbarColor("#303032")
//                                     .setToolbarTextColor("#FFFFFF")
//                                     .setToolbarIconColor("#FFFFFF")
//                                     .setStatusBarColor("#303032")
//                                     .setProgressBarColor("#35b18e")
//                                     .setButtonColor("#35b18e")
//                                    .setAlwaysShowDoneButton(true)
//                                    .setRequestCode(RESULT_LOAD_IMAGE__ONLY_NO_CROP)
//                                    .setKeepScreenOn(true)
//                                    .setIsCrop(false)
//                                    .setMediaType(Constant.IMAGE)
//                                    .start()


                            }


                            RESULT_LOAD_VIDEO_ONLY -> {

                                isClickButton(
                                    mIsCrop = false,
                                    mMultipleMode = false,
                                    mMediaType = Constant.VIDEO,
                                    mRequestCode = RESULT_LOAD_VIDEO_ONLY
                                )
//                                VasuImagePicker.ActivityBuilder(this)
//                                    .setFolderMode(true)
//                                    .setFolderTitle("Gallery")
//                                    .setMultipleMode(false)
//                                    .setImageCount(1)
//                                    .setMaxSize(10)
//                                    .setBackgroundColor("#232326")
//                                    .setToolbarColor("#303032")
//                                    .setToolbarTextColor("#FFFFFF")
//                                    .setToolbarIconColor("#FFFFFF")
//                                    .setStatusBarColor("#303032")
//                                    .setProgressBarColor("#35b18e")
//                                    .setButtonColor("#35b18e")
//                                    .setAlwaysShowDoneButton(true)
//                                    .setRequestCode(RESULT_LOAD_VIDEO_ONLY)
//                                    .setKeepScreenOn(true)
//                                    .setIsCrop(false)
//                                    .setMediaType(Constant.VIDEO)
//                                    .start()
                            }


                            RESULT_LOAD_GALLERY_MULTIPLE -> {
                                isClickButton(
                                    mIsCrop = false,
                                    mMultipleMode = true,
                                    mMediaType = Constant.BOTH,
                                    mRequestCode = RESULT_LOAD_GALLERY_MULTIPLE
                                )
//                                VasuImagePicker.ActivityBuilder(this)
//                                    .setFolderMode(true)
//                                    .setFolderTitle("Gallery")
//                                    .setMultipleMode(true)
//                                    .setMaxSize(10)
//                                    .setBackgroundColor("#232326")
//                                    .setToolbarColor("#303032")
//                                    .setToolbarTextColor("#FFFFFF")
//                                    .setToolbarIconColor("#FFFFFF")
//                                    .setStatusBarColor("#303032")
//                                    .setProgressBarColor("#35b18e")
//                                    .setButtonColor("#35b18e")
//                                    .setAlwaysShowDoneButton(true)
//                                    .setRequestCode(RESULT_LOAD_GALLERY_MULTIPLE)
//                                    .setKeepScreenOn(true)
//                                    .start()
                            }


                            RESULT_LOAD_IMAGE_MULTIPLE -> {
                                isClickButton(
                                    mIsCrop = false,
                                    mMultipleMode = true,
                                    mMediaType = Constant.IMAGE,
                                    mRequestCode = RESULT_LOAD_IMAGE_MULTIPLE
                                )
//                                VasuImagePicker.ActivityBuilder(this)
//                                    .setFolderMode(true)
//                                    .setFolderTitle("Gallery")
//                                    .setMultipleMode(true)
//                                    .setMaxSize(10)
//                                    .setBackgroundColor("#232326")
//                                    .setToolbarColor("#303032")
//                                    .setToolbarTextColor("#FFFFFF")
//                                    .setToolbarIconColor("#FFFFFF")
//                                    .setStatusBarColor("#303032")
//                                    .setProgressBarColor("#35b18e")
//                                    .setButtonColor("#35b18e")
//                                    .setAlwaysShowDoneButton(true)
//                                    .setRequestCode(RESULT_LOAD_IMAGE_MULTIPLE)
//                                    .setKeepScreenOn(true)
//                                    .setIsCrop(false)
//                                    .setMediaType(Constant.IMAGE)
//                                    .start()
                            }


                            RESULT_LOAD_VIDEO_MULTIPLE -> {
                                isClickButton(
                                    mIsCrop = false,
                                    mMultipleMode = true,
                                    mMediaType = Constant.VIDEO,
                                    mRequestCode = RESULT_LOAD_VIDEO_MULTIPLE
                                )
//                                VasuImagePicker.ActivityBuilder(this)
//                                    .setFolderMode(true)
//                                    .setFolderTitle("Gallery")
//                                    .setMultipleMode(true)
//                                    .setMaxSize(10)
//                                    .setBackgroundColor("#232326")
//                                    .setToolbarColor("#303032")
//                                    .setToolbarTextColor("#FFFFFF")
//                                    .setToolbarIconColor("#FFFFFF")
//                                    .setStatusBarColor("#303032")
//                                    .setProgressBarColor("#35b18e")
//                                    .setButtonColor("#35b18e")
//                                    .setAlwaysShowDoneButton(true)
//                                    .setRequestCode(RESULT_LOAD_VIDEO_MULTIPLE)
//                                    .setKeepScreenOn(true)
//                                    .setIsCrop(false)
//                                    .setMediaType(Constant.VIDEO)
//                                    .start()
                            }
                        }
                    } else {
                        val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        if (!showRationale) {
                            val builder = android.app.AlertDialog.Builder(this)
                            builder.setTitle("Permission Required")
                            builder.setMessage("Storage Permission are required to save Audios into External Storage")
                            builder.setPositiveButton("OK") { dialog, _ ->
//                                commen.isOutApp = true
                                dialog.dismiss()
                                startInstalledAppDetailsActivity(this)
                            }
                            builder.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
                            builder.create().show()
                        }
                    }
                }

            }

            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.gallery.setOnClickListener {
            bind.singleImage.isClickable = false
            bind.singleImage.isEnabled = false
            gallaeryType = RESULT_LOAD_GALLERY
            if (checkPermissions()) {
                Log.d("TAG", "onCreate: gdbvdfgdfgdgdfgdfg")
                isClickButton(
                    mIsCrop = false,
                    mMultipleMode = false,
                    mMediaType = Constant.BOTH,
                    mRequestCode = RESULT_LOAD_GALLERY
                )
            } else {
                requestPermissions()
            }
        }





        bind.galleryCrop.setOnClickListener {
            bind.singleImage.isClickable = false
            bind.singleImage.isEnabled = false
            gallaeryType = RESULT_LOAD_GALLERY_CROP
            if (checkPermissions()) {
                Log.d("TAG", "onCreate: gdbvdfgdfgdgdfgdfg")
                isClickButton(
                    mIsCrop = true,
                    mMultipleMode = false,
                    mMediaType = Constant.BOTH,
                    mRequestCode = RESULT_LOAD_GALLERY_CROP
                )
            } else {
                requestPermissions()
            }
        }




        bind.galleryOnlyImage.setOnClickListener {
            bind.singleImage.isClickable = false
            bind.singleImage.isEnabled = false
            gallaeryType = RESULT_LOAD_IMAGE_ONLY
            if (checkPermissions()) {
                Log.d("TAG", "onCreate: gdbvdfgdfgdgdfgdfg")
                isClickButton(
                    mIsCrop = true,
                    mMultipleMode = false,
                    mMediaType = Constant.IMAGE,
                    mRequestCode = RESULT_LOAD_IMAGE_ONLY
                )
            } else {
                requestPermissions()
            }
        }



        bind.galleryOnlyImageNoCrop.setOnClickListener {
            bind.singleImage.isClickable = false
            bind.singleImage.isEnabled = false
            gallaeryType = RESULT_LOAD_IMAGE__ONLY_NO_CROP
            if (checkPermissions()) {
                Log.d("TAG", "onCreate: gdbvdfgdfgdgdfgdfg")
                isClickButton(
                    mIsCrop = false,
                    mMultipleMode = false,
                    mMediaType = Constant.IMAGE,
                    mRequestCode = RESULT_LOAD_IMAGE__ONLY_NO_CROP
                )
            } else {
                requestPermissions()
            }
        }




        bind.galleryOnlyVideo.setOnClickListener {
            bind.singleImage.isClickable = false
            bind.singleImage.isEnabled = false
            gallaeryType = RESULT_LOAD_VIDEO_ONLY
            if (checkPermissions()) {
                Log.d("TAG", "onCreate: gdbvdfgdfgdgdfgdfg")
                isClickButton(
                    mIsCrop = false,
                    mMultipleMode = false,
                    mMediaType = Constant.VIDEO,
                    mRequestCode = RESULT_LOAD_VIDEO_ONLY
                )
            } else {
                requestPermissions()
            }
        }




        bind.gallaryMultiMode.setOnClickListener {
            bind.singleImage.isClickable = false
            bind.singleImage.isEnabled = false
            gallaeryType = RESULT_LOAD_GALLERY_MULTIPLE
            if (checkPermissions()) {
                Log.d("TAG", "onCreate: gdbvdfgdfgdgdfgdfg")
                isClickButton(
                    mIsCrop = false,
                    mMultipleMode = true,
                    mMediaType = Constant.BOTH,
                    mRequestCode = RESULT_LOAD_GALLERY_MULTIPLE
                )
            } else {
                requestPermissions()
            }
        }




        bind.gallaryMultiModeImages.setOnClickListener {
            bind.singleImage.isClickable = false
            bind.singleImage.isEnabled = false
            gallaeryType = RESULT_LOAD_IMAGE_MULTIPLE
            if (checkPermissions()) {
                Log.d("TAG", "onCreate: gdbvdfgdfgdgdfgdfg")
                isClickButton(
                    mIsCrop = false,
                    mMultipleMode = true,
                    mMediaType = Constant.IMAGE,
                    mRequestCode = RESULT_LOAD_IMAGE_MULTIPLE
                )
            } else {
                requestPermissions()
            }
        }




        bind.gallaryMultiModeVideo.setOnClickListener {
            bind.singleImage.isClickable = false
            bind.singleImage.isEnabled = false
            gallaeryType = RESULT_LOAD_VIDEO_MULTIPLE
            if (checkPermissions()) {
                Log.d("TAG", "onCreate: gdbvdfgdfgdgdfgdfg")
                isClickButton(
                    mIsCrop = false,
                    mMultipleMode = true,
                    mMediaType = Constant.VIDEO,
                    mRequestCode = RESULT_LOAD_VIDEO_MULTIPLE
                )
            } else {
                requestPermissions()
            }
        }



        bind.singleImage.setOnClickListener {
            Log.d("TAG", "onCreate: werwerwrwerwerewrewrewewrewrersfw")
//            config = VasuImagePicker.ActivityBuilder(this).config
//            config?.setStatusBarColor("#C197D2")
//            config?.setToolbarColor("#C197D2")
//            config?.setBackgroundColor("#D3B1C2")
//            config?.setToolbarTextColor("#211522")
//            config?.setToolbarIconColor("#211522")
//            config?.setProgressBarColor("#C197D2")
//            config?.setButtonColor("#613659")

            CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setGuidelinesColor(Color.WHITE)
                .setBorderCornerColor(Color.WHITE)
                .setBorderLineColor(Color.WHITE)
                .start(this)

        }




        adapterImage = ImageRecyclerAdapter(this, arrayListOf<Uri>())
        bind.imageRv.adapter = adapterImage
        bind.imageRv.layoutManager = GridLayoutManager(this, 3)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            RESULT_LOAD_GALLERY -> {
                if (resultCode == RESULT_OK) {
                    Log.d(
                        "TAG",
                        "onActivityResult1: RESULT_LOAD_GALLERY = ${data?.extras?.get("data")}"
                    )
                    val imageUri = data?.extras?.get("data")
                    try {
                        Log.d("TAG", "onActivityResult2: fdsfsdfdsfdsfdsfdsfdsf $imageUri")

                        Glide.with(this).asBitmap().load(imageUri).diskCacheStrategy(
                            DiskCacheStrategy.ALL
                        ).priority(Priority.IMMEDIATE).skipMemoryCache(true)
                            .addListener(object : RequestListener<Bitmap?> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Bitmap?>?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    Log.d("TAG", "onLoadFailed: cvxvcvcv")
                                    bind.singleImage.visibility = View.GONE
                                    bind.imageRv.visibility = View.GONE
                                    return true
                                }

                                override fun onResourceReady(
                                    resource: Bitmap?,
                                    model: Any?,
                                    target: Target<Bitmap?>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    Log.d("TAG", "onResourceReady: ")

                                    bind.singleImage.setImageBitmap(resource)
                                    return true
                                }
                            }).into(bind.singleImage)
                        bind.singleImage.visibility = View.VISIBLE
                        bind.imageRv.visibility = View.GONE
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            RESULT_LOAD_GALLERY_CROP -> {
                if (resultCode == RESULT_OK) {
                    Log.d("TAG1", "onActivityResult1: RESULT_LOAD_GALLERY_CROP $resultCode")
                    Log.d(
                        "TAG1",
                        "onActivityResult1: RESULT_LOAD_GALLERY_CROP = ${
                            data?.getStringExtra(ImagePickerActivity.EXTRA_SELECTED_URI)
                        }"
                    )

                    val imageUri = data?.getStringExtra(ImagePickerActivity.EXTRA_SELECTED_URI)
                    try {
                        Glide.with(this).asBitmap().load(imageUri).diskCacheStrategy(
                            DiskCacheStrategy.ALL
                        ).priority(Priority.IMMEDIATE).skipMemoryCache(true)
                            .addListener(object : RequestListener<Bitmap?> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Bitmap?>?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    Log.d("TAG", "onLoadFailed: cvxvcvcv")
                                    bind.singleImage.visibility = View.GONE

                                    bind.imageRv.visibility = View.GONE
                                    return true
                                }

                                override fun onResourceReady(
                                    resource: Bitmap?,
                                    model: Any?,
                                    target: Target<Bitmap?>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    Log.d("TAG", "onResourceReady: ")

                                    bind.singleImage.setImageBitmap(resource)
                                    return true
                                }
                            }).into(bind.singleImage)
                        bind.singleImage.visibility = View.VISIBLE
                        bind.imageRv.visibility = View.GONE

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            RESULT_LOAD_IMAGE_ONLY -> {
                if (resultCode == RESULT_OK) {
                    Log.d(
                        "TAG",
                        "onActivityResult1: RESULT_LOAD_IMAGE_ONLY = ${
                            data?.getStringExtra(ImagePickerActivity.EXTRA_SELECTED_URI)
                        }"
                    )
                    val imageUri = data?.getStringExtra(ImagePickerActivity.EXTRA_SELECTED_URI)
                    try {

                        Glide.with(this).asBitmap().load(imageUri).diskCacheStrategy(
                            DiskCacheStrategy.ALL
                        ).priority(Priority.IMMEDIATE).skipMemoryCache(true)
                            .addListener(object : RequestListener<Bitmap?> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Bitmap?>?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    Log.d("TAG", "onLoadFailed: cvxvcvcv")
                                    bind.singleImage.visibility = View.GONE

                                    bind.imageRv.visibility = View.GONE
                                    return true
                                }

                                override fun onResourceReady(
                                    resource: Bitmap?,
                                    model: Any?,
                                    target: Target<Bitmap?>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    Log.d("TAG", "onResourceReady: ")

                                    bind.singleImage.setImageBitmap(resource)
                                    return true
                                }
                            }).into(bind.singleImage)
                        bind.singleImage.visibility = View.VISIBLE

                        bind.imageRv.visibility = View.GONE

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            RESULT_LOAD_IMAGE__ONLY_NO_CROP -> {
                if (resultCode == RESULT_OK) {
                    Log.d(
                        "TAG",
                        "onActivityResult1: RESULT_LOAD_IMAGE__ONLY_NO_CROP = ${data?.extras?.get("data")}"
                    )

                    val imageUri = data?.extras?.get("data")
                    uri = imageUri?.toString()?.toUri()!!
                    try {
                        Glide.with(this).asBitmap().load(imageUri).diskCacheStrategy(
                            DiskCacheStrategy.ALL
                        ).priority(Priority.IMMEDIATE).skipMemoryCache(true)
                            .addListener(object : RequestListener<Bitmap?> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Bitmap?>?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    Log.d("TAG", "onLoadFailed: cvxvcvcv")
                                    bind.singleImage.visibility = View.GONE

                                    bind.imageRv.visibility = View.GONE
                                    return true
                                }

                                override fun onResourceReady(
                                    resource: Bitmap?,
                                    model: Any?,
                                    target: Target<Bitmap?>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    Log.d("TAG", "onResourceReady: ")

                                    bind.singleImage.setImageBitmap(resource)
                                    return true
                                }
                            }).into(bind.singleImage)
//                        bind.singleImage.setImageBitmap(bitmap)
                        bind.singleImage.visibility = View.VISIBLE
                        bind.imageRv.visibility = View.GONE
                        bind.singleImage.isEnabled = true
                        bind.singleImage.isClickable = true

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            RESULT_LOAD_VIDEO_ONLY -> {
                if (resultCode == RESULT_OK) {
                    Log.d(
                        "TAG",
                        "onActivityResult1: RESULT_LOAD_VIDEO_ONLY = ${data?.extras?.get("data")}"
                    )
                    val imageUri: Uri = data?.extras?.get("data").toString().toUri()
                    try {
                        Glide.with(this).asBitmap().load(data?.extras?.get("data"))
                            .diskCacheStrategy(
                                DiskCacheStrategy.ALL
                            ).priority(Priority.IMMEDIATE).skipMemoryCache(true)
                            .addListener(object : RequestListener<Bitmap?> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Bitmap?>?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    Log.d("TAG", "onLoadFailed: cvxvcvcv")
                                    bind.singleImage.visibility = View.GONE
                                    bind.imageRv.visibility = View.GONE
                                    return true
                                }

                                override fun onResourceReady(
                                    resource: Bitmap?,
                                    model: Any?,
                                    target: Target<Bitmap?>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    Log.d("TAG", "onResourceReady: ")

                                    bind.singleImage.setImageBitmap(resource)
                                    return true
                                }
                            }).into(bind.singleImage)
                        bind.singleImage.visibility = View.VISIBLE
                        bind.imageRv.visibility = View.GONE
//                        bind.singleVideo.setVideoURI(imageUri)


                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            RESULT_LOAD_GALLERY_MULTIPLE -> {
                if (resultCode == RESULT_OK) {
                    Log.d(
                        "TAG",
                        "onActivityResult1: RESULT_LOAD_GALLERY_MULTIPLE = ${data?.extras?.get("imageData")}"
                    )
                    val list: ArrayList<Uri> = data?.extras?.get("imageData") as ArrayList<Uri>
                    bind.singleImage.visibility = View.GONE
                    adapterImage.updateList(list)
                    bind.imageRv.visibility = View.VISIBLE
                }
            }

            RESULT_LOAD_IMAGE_MULTIPLE -> {
                if (resultCode == RESULT_OK) {
                    Log.d(
                        "TAG",
                        "onActivityResult1: RESULT_LOAD_IMAGE_MULTIPLE = ${data?.extras?.get("imageData")}"
                    )
                    val list: ArrayList<Uri> = data?.extras?.get("imageData") as ArrayList<Uri>
                    adapterImage.updateList(list)
                    bind.singleImage.visibility = View.GONE
                    bind.imageRv.visibility = View.VISIBLE
                }
            }

            RESULT_LOAD_VIDEO_MULTIPLE -> {
                if (resultCode == RESULT_OK) {
                    Log.d(
                        "TAG",
                        "onActivityResult1: RESULT_LOAD_IMAGE_MULTIPLE = ${data?.extras?.get("imageData")}"
                    )
                    val list: ArrayList<Uri> = data?.extras?.get("imageData") as ArrayList<Uri>
                    adapterImage.updateList(list)
                    bind.singleImage.visibility = View.GONE
                    bind.imageRv.visibility = View.VISIBLE
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {

                if (resultCode == RESULT_OK) {
                    Log.d(
                        "TAG",
                        "onActivityResult: fsfsdfdsfdsfsdfsdfsdf ${CropImage.getActivityResult(data)?.uri}"
                    )
                    val imageUri = CropImage.getActivityResult(data)?.uri
                    uri = imageUri!!
                    Glide.with(this).asBitmap().load(imageUri).diskCacheStrategy(
                        DiskCacheStrategy.ALL
                    ).priority(Priority.IMMEDIATE).skipMemoryCache(true)
                        .addListener(object : RequestListener<Bitmap?> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Bitmap?>?,
                                isFirstResource: Boolean,
                            ): Boolean {
                                Log.d("TAG", "onLoadFailed: cvxvcvcv")
                                bind.singleImage.visibility = View.GONE

                                bind.imageRv.visibility = View.GONE
                                return true
                            }

                            override fun onResourceReady(
                                resource: Bitmap?,
                                model: Any?,
                                target: Target<Bitmap?>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean,
                            ): Boolean {
                                Log.d("TAG", "onResourceReady: ")

                                bind.singleImage.setImageBitmap(resource)
                                return true
                            }
                        }).into(bind.singleImage)
                    bind.singleImage.visibility = View.VISIBLE
                    bind.imageRv.visibility = View.GONE
                }
            }


            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }


    private var PERMISSIONS = arrayOf<String>()

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PERMISSIONS = arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            PERMISSIONS = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        ActivityCompat.requestPermissions(
            this,
            PERMISSIONS,
            1001
        )
    }

    fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d(
                "TAG", "checkPermissions: ${
                    PermissionChecker.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_VIDEO
                    ) == PermissionChecker.PERMISSION_GRANTED
                }"
            )
            PermissionChecker.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_VIDEO
            ) == PermissionChecker.PERMISSION_GRANTED
        } else {
            PermissionChecker.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PermissionChecker.PERMISSION_GRANTED
        }
    }

    fun startInstalledAppDetailsActivity(context: Activity?) {
        if (context == null) {
            return
        }
        val i = Intent()
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:" + context.packageName)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        context.startActivity(i)
    }
}