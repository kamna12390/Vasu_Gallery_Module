package com.vasu.image.video.pickrandom.galleryapp.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imagecrop.Crop.CropImageView
import com.vasu.image.video.pickrandom.galleryapp.R
import com.vasu.image.video.pickrandom.galleryapp.adapter.AlbumAdapter
import com.vasu.image.video.pickrandom.galleryapp.adapter.AlbumAdapterVideo
import com.vasu.image.video.pickrandom.galleryapp.adapter.ImageAdapter
import com.vasu.image.video.pickrandom.galleryapp.adapter.ImageAdapterVideo
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant
import com.vasu.image.video.pickrandom.galleryapp.model.Album
import com.vasu.image.video.pickrandom.galleryapp.model.Media
import com.vasu.image.video.pickrandom.galleryapp.util.CropImage
import com.vasu.image.video.pickrandom.galleryapp.util.GalleryUtil
import gun0912.tedimagepicker.builder.type.MediaType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ImagePickerActivity : AppCompatActivity() {

    private lateinit var mainConstraint: ConstraintLayout

    //Album Activity
    private lateinit var disposableImage: Disposable
    private lateinit var disposableVideo: Disposable
    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var albumAdapterVideo: AlbumAdapterVideo
    private lateinit var mRVAlbum: RecyclerView
    private lateinit var mRVAlbumVideo: RecyclerView
    private lateinit var image: Button
    private lateinit var video: Button
    private lateinit var mPBAlbum: ProgressBar
    private lateinit var mAlbumListImages: List<Album>
    private lateinit var mAlbumListVideo: List<Album>
    private lateinit var mSelectedImagesUri : ArrayList<Uri>
    private var uri: Uri? = null

    //Image Activity
    private lateinit var mImageAdapter: ImageAdapter
    private lateinit var mImageAdapterVideo: ImageAdapterVideo
    private lateinit var mRVImage: RecyclerView
    private lateinit var mRVImageVideo: RecyclerView
    private lateinit var mImageList: List<Album>
    private lateinit var path: Uri

    //Ucrop Fragment
    private var position = 0
    private var mToolbarColor = 0
    private var mStatusBarColor = 0
    private var mToolbarWidgetColor = 0
    private var mToolbarTitle: String? = null
    private var isCropViewOpen = false

    @DrawableRes
    private var mToolbarCancelDrawable = 0

    @DrawableRes
    private var mToolbarCropDrawable = 0
    private var croptoolbar: Toolbar? = null
    lateinit var toolbarTitle: TextView
    lateinit var no_data_found: TextView
    lateinit var txtFolderName: TextView
    lateinit var toolbar : ConstraintLayout
    lateinit var toolbarImage : ConstraintLayout
    lateinit var imgBack : ImageView
    lateinit var imgBackImage : ImageView
    lateinit var imgDoneImage : ImageView
    lateinit var imgDoneImageMain : ImageView
    //    lateinit var imgDoneImageMain1 : ImageView
    private var mShowLoader = false

    var builder: AlertDialog.Builder? = null
    var alert: AlertDialog? = null


    companion object {
        const val EXTRA_SELECTED_URI = "EXTRA_SELECTED_URI"
        private const val EXTRA_SELECTED_RATIO = "EXTRA_SELECTED_RATIO"
        private const val TAG = "ImagePickerActivity"
        private const val SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage.png"
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_picker)
        toolbarTitle = findViewById(R.id.toolbarTitle)
        no_data_found = findViewById(R.id.no_data_found)
        toolbar = findViewById(R.id.toolbar)
        toolbarImage = findViewById(R.id.toolbarImage)
        imgBack = findViewById(R.id.imgBack)
        imgBackImage = findViewById(R.id.imgBackImage)
        if(Constant.backIcon!=null) {
            imgBack.setImageDrawable(Constant.backIcon!!)
            imgBackImage.setImageDrawable(Constant.backIcon!!)
        }
        else{
            imgBack.setImageResource(R.drawable.ic_back_arrow_test_1)
            imgBackImage.setImageResource(R.drawable.ic_back_arrow_test_1)
        }

        mainConstraint = findViewById(R.id.mainConstraint)
        imgDoneImage = findViewById(R.id.imgDoneImage)
        imgDoneImageMain = findViewById(R.id.imgDoneImageMain)
        if(Constant.doneIcon!=null) {
            imgDoneImage.setImageDrawable(Constant.doneIcon)
            imgDoneImageMain.setImageDrawable(Constant.doneIcon)
        }
        else{
            imgDoneImage.setImageResource(R.drawable.ic_done)
            imgDoneImageMain.setImageResource(R.drawable.ic_done)
        }
//        imgDoneImageMain1 = findViewById(R.id.imgDoneImageMain1)
        txtFolderName = findViewById(R.id.txtFolderName)
        mRVAlbumVideo = findViewById(R.id.rv_album_video)
        mRVImageVideo = findViewById(R.id.rv_image_video)
        image = findViewById(R.id.btnimage)
        video = findViewById(R.id.btnvideo)
        if (!checkWriteExternalPermission()) finish()
        initView()

        Constant.lastSelectedPosition = -1

        if (Constant.isKeepScreenOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        if(!Constant.isCrop!!){
            image.visibility = VISIBLE
            video.visibility = VISIBLE
        }
        else{
            image.visibility = GONE
            video.visibility = GONE

        }
        mSelectedImagesUri = arrayListOf()
        mSelectedImagesUri.clear()

        Log.d(TAG, "onCreate: ${Constant.mediaType}")

        builder = AlertDialog.Builder(this@ImagePickerActivity)
        val window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(Constant.statusBarColor!=null) {
                window.statusBarColor = Color.parseColor(Constant.statusBarColor)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(Constant.progressBarColor!=null) {
                mPBAlbum.indeterminateTintList =
                    ColorStateList.valueOf(Color.parseColor(Constant.progressBarColor))
            }
        }
        if(Constant.toolbarTextColor!=null){
            toolbarTitle.setTextColor(Color.parseColor(Constant.toolbarTextColor))
            image.setTextColor(Color.parseColor(Constant.toolbarTextColor))
            video.setTextColor(Color.parseColor(Constant.toolbarTextColor))
            txtFolderName.setTextColor(Color.parseColor(Constant.toolbarTextColor))
        }

        if(Constant.toolbarColor!=null) {
            toolbar.setBackgroundColor(Color.parseColor(Constant.toolbarColor))
            toolbarImage.setBackgroundColor(Color.parseColor(Constant.toolbarColor))
        }

        if(Constant.toolbarIconColor!=null){
            imgBack.setColorFilter(Color.parseColor(Constant.toolbarIconColor))
            imgBackImage.setColorFilter(Color.parseColor(Constant.toolbarIconColor))
            imgDoneImage.setColorFilter(Color.parseColor(Constant.toolbarIconColor))
            imgDoneImageMain.setColorFilter(Color.parseColor(Constant.toolbarIconColor))
        }



        toolbarTitle.text = Constant.folderTitle

        if(Constant.backgroundColor!=null) {
            mainConstraint.setBackgroundColor(Color.parseColor(Constant.backgroundColor))
        }

        if(Constant.buttonColor!=null) {
            image.setBackgroundColor(Color.parseColor(Constant.buttonColor!!))
            video.setBackgroundColor(Color.parseColor(Constant.buttonColor!!))
            no_data_found.setTextColor(Color.parseColor(Constant.buttonColor))
        }






//        no_data_found.setTextColor(Constant.toolbarTextColor)

        if (checkWriteExternalPermission()) {
            loadMedia(true)
        }

        if(Constant.mediaType != Constant.BOTH) {
            if (Constant.mediaType?.equals(Constant.IMAGE)!!) {
                disposableVideo.dispose()
                Log.d(TAG, "onCreate: efgdsgsgdsgsdgg")
                image.visibility = GONE
                video.visibility = GONE
                mRVAlbum.visibility = VISIBLE
                mRVAlbumVideo.visibility = GONE
                mRVImage.visibility = GONE
                mRVImageVideo.visibility = GONE
            } else if (Constant.mediaType?.equals(Constant.VIDEO)!!) {
                disposableImage.dispose()
                Log.d(TAG, "onCreate: efgdsgsgdsgsdgg 1")
                image.visibility = GONE
                video.visibility = GONE
                mRVAlbum.visibility = GONE
                mRVAlbumVideo.visibility = VISIBLE
                mRVImage.visibility = GONE
                mRVImageVideo.visibility = GONE
            }
        }
//        loadMedia(true)
        mAlbumListImages = arrayListOf()
        mAlbumListVideo = arrayListOf()
        setStatusBarColor()
        imgBack.setOnClickListener {
            onBackPressed()
        }

        imgBackImage.setOnClickListener {
            onBackPressed()
        }

        imgDoneImage.setOnClickListener {

            setResult(RESULT_OK , Intent().putExtra("imageData" , mSelectedImagesUri))
            finish()
        }
        imgDoneImageMain.setOnClickListener {
            Log.d(TAG, "onCreate: ${mSelectedImagesUri.size}")
            setResult(RESULT_OK , Intent().putExtra("imageData" , mSelectedImagesUri))
            finish()

        }
//        imgDoneImageMain1.setOnClickListener {
//            val uri = path
//            if(ImageAdapter.isLongClick){
//                ImageAdapter.isLongClick = false
//            }
//
//        }

        image.setOnClickListener {

            if(mAlbumListImages[0].mediaUris.isNotEmpty()){
                Log.d(TAG, "check progressbar visibility : 1 ")
                mPBAlbum.visibility = View.GONE
                mRVAlbum.visibility = VISIBLE
                mRVAlbumVideo.visibility = GONE
                mRVImage.visibility = GONE
                mRVImageVideo.visibility = GONE
                imgDoneImage.visibility = View.INVISIBLE
                imgDoneImageMain.visibility = View.INVISIBLE
                no_data_found.visibility = GONE
            }
            else{
                Log.d(TAG, "check progressbar visibility : 2 ")
                mPBAlbum.visibility = View.GONE
                mRVAlbum.visibility = GONE
                mRVAlbumVideo.visibility = GONE
                mRVImage.visibility = GONE
                mRVImageVideo.visibility = GONE
                imgDoneImage.visibility = View.INVISIBLE
                imgDoneImageMain.visibility = View.INVISIBLE
                no_data_found.visibility = VISIBLE
            }
//            imgDoneImageMain1.visibility = View.INVISIBLE
            mSelectedImagesUri.clear()
            for (data in mAlbumListVideo){
                for (data1 in data.mediaUris){
                    data1.isSelected =  false
                }
            }
        }
        video.setOnClickListener {
            if(mAlbumListVideo[0].mediaUris.isNotEmpty()){
                Log.d(TAG, "check progressbar visibility : 3 ")
                mPBAlbum.visibility = View.GONE
                mRVAlbum.visibility = GONE
                mRVAlbumVideo.visibility = VISIBLE
                mRVImage.visibility = GONE
                mRVImageVideo.visibility = GONE
                imgDoneImage.visibility = View.INVISIBLE
                imgDoneImageMain.visibility = View.INVISIBLE
                no_data_found.visibility = GONE
            }
            else{
                Log.d(TAG, "check progressbar visibility : 4 ")
                mPBAlbum.visibility = View.GONE
                mRVAlbum.visibility = GONE
                mRVAlbumVideo.visibility = GONE
                mRVImage.visibility = GONE
                mRVImageVideo.visibility = GONE
                imgDoneImage.visibility = View.INVISIBLE
                imgDoneImageMain.visibility = View.INVISIBLE
                no_data_found.visibility = VISIBLE
            }

//            imgDoneImageMain1.visibility = View.INVISIBLE
            mSelectedImagesUri.clear()
            for (data in mAlbumListImages){
                for (data1 in data.mediaUris){
                    data1.isSelected =  false
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (!checkWriteExternalPermission()) onBackPressed()
    }

    private fun checkWriteExternalPermission(): Boolean {
        var permissionWriteExternalStorage :String? = null
        var permissionReadImage :String? = null
        var permissionReadVide :String? = null
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            permissionReadImage = Manifest.permission.READ_MEDIA_IMAGES
            permissionReadVide = Manifest.permission.READ_MEDIA_VIDEO
        }
        else{
            permissionWriteExternalStorage =  android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
        return if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            checkCallingOrSelfPermission(permissionReadImage!!)==PackageManager.PERMISSION_GRANTED && checkCallingOrSelfPermission(permissionReadVide!!)==PackageManager.PERMISSION_GRANTED
        }
        else{
            checkCallingOrSelfPermission(permissionWriteExternalStorage!!) == PackageManager.PERMISSION_GRANTED
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setStatusBarColor() {
        val window: Window = window
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = Color.parseColor("#6992C6")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                if(Constant.toolbarColor!=null) {
                    window.statusBarColor = Color.parseColor(Constant.toolbarColor)
                }
            }
        }
    }







//    private fun setupViews(extras: Bundle?) {
//        mStatusBarColor = Color.parseColor(Constant.statusBarColor)
//        mToolbarColor = Color.parseColor(Constant.toolbarColor)
//        mToolbarCancelDrawable = R.drawable.ic_close_
//        mToolbarCropDrawable = getDrawable(Constant.doneIcon)
//        mToolbarWidgetColor = ContextCompat.getColor(this@ImagePickerActivity, R.color.black)
//        mToolbarTitle = "Crop Image"
//        setupAppBar()
//    }

    private fun setupAppBar() {
        croptoolbar = findViewById(R.id.croptoolbarAlbum)

        // Set Toolbar Color
        croptoolbar!!.setBackgroundColor(Color.parseColor(Constant.toolbarColor))
        croptoolbar!!.setTitleTextColor(Color.parseColor(Constant.toolbarTextColor))
        croptoolbar!!.visibility = View.VISIBLE

        toolbarImage.visibility = View.GONE
        mRVImage.visibility = View.GONE


        val cropToolBarTitle = croptoolbar!!.findViewById<TextView>(R.id.toolbar_title)!!
        cropToolBarTitle.setTextColor(Color.parseColor(Constant.toolbarTextColor))
        cropToolBarTitle.text = mToolbarTitle

        // Color Toolbar Icons
        val stateButtonDrawable = ContextCompat.getDrawable(baseContext, mToolbarCancelDrawable)
        if (stateButtonDrawable != null) {
            stateButtonDrawable.mutate()
            stateButtonDrawable.setColorFilter(Color.parseColor(Constant.toolbarIconColor), PorterDuff.Mode.SRC_ATOP)
            croptoolbar!!.navigationIcon = stateButtonDrawable
        }
        setSupportActionBar(croptoolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
    }


    private fun initView() {
        mRVAlbum = findViewById(R.id.rv_album)
        mRVImage = findViewById(R.id.rv_image)
        mPBAlbum = findViewById(R.id.pb_album)
        Log.d(TAG, "initView: ${Constant.isCrop}")


    }

    private fun loadMedia(isRefresh: Boolean = false) {
        disposableImage = GalleryUtil.getMedia(this, MediaType.IMAGE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { albumList: List<Album> ->
                mAlbumListImages = albumList
                for (i in albumList) {
                    Log.d("TAG", "loadMedia1: ${i.mediaUris.size}")
                }
                Log.d(TAG, "loadMedia3232: ${albumList[0].name}")
                var layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                layoutManager = GridLayoutManager(this, 2)
                albumAdapter = AlbumAdapter(
                    this@ImagePickerActivity,
                    if(Constant.buttonColor!=null){Color.parseColor(Constant.buttonColor!!)}else{resources.getColor(R.color.green)},
                    mAlbumListImages,
                    object : AlbumAdapter.OnAlbumSelected {
                        override fun onAlbumClicked(
                            position: Int,
                            folderName: String
                        ) {
                            if(mSelectedImagesUri.isNotEmpty()){
                                imgDoneImage.visibility = View.VISIBLE
                            }
                            setImageAdapter(position, folderName)
                            toolbarImage.visibility = View.VISIBLE
                            txtFolderName.text = folderName

                        }

                    })
                if(Constant.mediaType != Constant.BOTH) {
                    if (Constant.mediaType.equals(Constant.IMAGE)) {
                        if(albumList[0].mediaUris.size==0){
                            Log.d(TAG, "check progressbar visibility : 5 ")

                            mPBAlbum.visibility = View.GONE
                            mRVAlbum.visibility = View.GONE
                            no_data_found.visibility = VISIBLE
                        }
                        else{
                            Log.d(TAG, "check progressbar visibility : 6 ")
                            mRVAlbum.layoutManager = layoutManager
                            mRVAlbum.adapter = albumAdapter
                            mPBAlbum.visibility = View.GONE
                            mRVAlbum.visibility = View.VISIBLE
                            no_data_found.visibility = GONE
                        }
                    } else if (Constant.mediaType.equals(Constant.VIDEO)) {
                        Log.d(TAG, "check progressbar visibility : 7 ")
                        mPBAlbum.visibility = View.GONE
                        mRVAlbum.visibility = View.GONE
                    }
                }
                else{
                    if(albumList[0].mediaUris.size!=0) {
                        Log.d(TAG, "check progressbar visibility : 8 ")
                        mRVAlbum.layoutManager = layoutManager
                        mRVAlbum.adapter = albumAdapter
                        mPBAlbum.visibility = View.GONE
                        mRVAlbum.visibility = View.VISIBLE
                        no_data_found.visibility = GONE
                    }
                    else{
                        Log.d(TAG, "check progressbar visibility : 9 ")
                        mPBAlbum.visibility = View.GONE
                        mRVAlbum.visibility = View.GONE
                        no_data_found.visibility = VISIBLE
                    }
                }
//                if(albumList[0].mediaUris.size!=0) {
//
//                    no_data_found.visibility = GONE
//                    Log.d(TAG, "check progressbar visibility : 10 ")
//                    mPBAlbum.visibility = View.GONE
//
//                }
//                else{
//                    no_data_found.visibility = VISIBLE
//                }

            }

        disposableVideo = GalleryUtil.getMedia(this , MediaType.VIDEO)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{albumList: List<Album> ->
                mAlbumListVideo = albumList
                for (i in mAlbumListVideo){
                    Log.d("TAG", "loadMediarwgdfsg: ${i.name}")
                }
                Log.d(TAG, "loadMedia3232: ${albumList[0].name} 3")
                var layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                layoutManager = GridLayoutManager(this, 2)
                albumAdapterVideo = AlbumAdapterVideo(
                    this@ImagePickerActivity,
                    if(Constant.buttonColor!=null){Color.parseColor(Constant.buttonColor!!)}else{resources.getColor(R.color.green)},
                    mAlbumListVideo,
                    object : AlbumAdapterVideo.OnAlbumSelected {
                        override fun onAlbumClicked(
                            position: Int,
                            folderName: String
                        ) {
                            Log.d(
                                TAG,
                                "onAlbumClicked: ${mAlbumListVideo[position].mediaUris.size}"
                            )
                            if(mSelectedImagesUri.isNotEmpty()){
                                imgDoneImage.visibility = View.VISIBLE
                            }
                            setImageAdapterVideo(position, folderName)
                            toolbarImage.visibility = View.VISIBLE
                            txtFolderName.text = folderName

                        }

                    })
                Log.d(TAG, "loadMedia3232: ${mAlbumListImages.isEmpty()} 3")
                if(mAlbumListImages.isEmpty()){
                    Log.d(TAG, "loadMedia3232: Constant ${Constant!=null} 3")
                    if(Constant.mediaType!=Constant.BOTH){
                        Log.d(TAG, "loadMedia3232: Constant ${Constant.mediaType} 3")
                        if(Constant.mediaType == Constant.VIDEO ){
                            Log.d(TAG, "check progressbar visibility : 11 ")
                            mPBAlbum.visibility = View.GONE
                        }
                    }
                    else{
                        Log.d(TAG, "loadMedia3232: else ${Constant.mediaType} 3")
                        Log.d(TAG, "check progressbar visibility visible: 1 ")
                        mPBAlbum.visibility = View.VISIBLE
                    }
                }
                else {
                    Log.d(TAG, "check progressbar visibility : 12 ")
                    mPBAlbum.visibility = View.GONE
                }
//                mRVAlbum.visibility = View.VISIBLE


//                if(albumList[0].mediaUris.size!=0) {
                    mRVAlbumVideo.layoutManager = layoutManager
                    mRVAlbumVideo.adapter = albumAdapterVideo
////                    no_data_found.visibility = GONE
//                }
//                else{
////                    no_data_found.visibility = VISIBLE
//                }

                if(Constant.mediaType!=Constant.BOTH){
                    if(Constant.mediaType==Constant.VIDEO){
                        if(albumList[0].mediaUris.isEmpty()){
                            no_data_found.visibility = VISIBLE
                            mRVAlbumVideo.visibility = GONE
                        }
                        else{
                            no_data_found.visibility = GONE
                            mRVAlbumVideo.visibility = VISIBLE
                        }
                    }
                }
                else{

                }

            }
    }

    private fun setImageAdapterVideo(position: Int, folderName: String) {
        Log.d(TAG, "setImageAdapterVideo: vdsvsdvsdsfdsfdsfdsff")
        toolbarImage.visibility = View.VISIBLE
        txtFolderName.text = folderName

        showVideo(true)

        Log.d(TAG, "setImageAdapterVideo: ${mRVImageVideo.visibility} == ${mAlbumListVideo[position].mediaUris}")

        var layoutManager = GridLayoutManager(this, 4)
        mImageAdapterVideo = ImageAdapterVideo(
            this@ImagePickerActivity,
            if(Constant.buttonColor!=null){Color.parseColor(Constant.buttonColor!!)}else{resources.getColor(R.color.green)},
            mAlbumListVideo[position].mediaUris,
            object : ImageAdapterVideo.OnImageClick {
                override fun selectMedia(uri: Media) {
                    if(Constant.isMultipleMode!!){
                        imgDoneImage.visibility = View.VISIBLE
                        imgDoneImageMain.visibility = View.VISIBLE
//                        imgDoneImageMain1.visibility = View.VISIBLE
                        uri.isSelected = !uri.isSelected
                        Log.d(TAG, "selectMedia: ${uri.isSelected}")
                        if(uri.isSelected){
                            mSelectedImagesUri.add(uri.uri)
                        }
                        else{
                            mSelectedImagesUri.remove(uri.uri)
                        }
                        if(mSelectedImagesUri.isEmpty()){
                            imgDoneImage.visibility = View.INVISIBLE
                            imgDoneImageMain.visibility = View.INVISIBLE
                        }
                        Log.d(TAG, "selectMedia: ${mSelectedImagesUri.size}")
                    }
                    else {
                        if (uri.toString().isNotEmpty()) {
                            imgDoneImage.visibility = View.INVISIBLE
                            imgDoneImageMain.visibility = View.INVISIBLE
//                            imgDoneImageMain1.visibility = View.INVISIBLE
                            path = uri.uri
                            if(Constant.isMultipleMode!!){
                                mSelectedImagesUri.add(uri.uri)
                                setResult(RESULT_OK, Intent().putExtra("imageData", mSelectedImagesUri))
                            }
                            else{
                                setResult(RESULT_OK, Intent().putExtra("data", uri.uri))
                            }
                            finish()
//                        startCrop(uri)

                        } else {
                            imgDoneImage.visibility = View.INVISIBLE
                            imgDoneImageMain.visibility = View.INVISIBLE
//                            imgDoneImageMain1.visibility = View.INVISIBLE
                            Toast.makeText(
                                this@ImagePickerActivity,
                                "Please Select Image",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }

            }, object : ImageAdapterVideo.OnCorruptClick {
                override fun onCorruptImageClicked() {
                    builder!!.setMessage("This Video is corrupted. Please select another Video.")
                    builder!!.setPositiveButton("Ok") { dialog, which -> dialog.dismiss() }
                    alert = builder!!.create()
                    alert!!.show()
                }

            })
        mRVImageVideo.layoutManager = layoutManager
        mRVImageVideo.adapter = mImageAdapterVideo
    }

    private fun setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
//make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
        val win: Window = activity.window
        val winParams: WindowManager.LayoutParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    override fun onBackPressed() {
//        if(ImageAdapter.isLongClick){
//            ImageAdapter.isLongClick = false
//        }
        when {
            isCropViewOpen -> {
            }
            mRVImage.visibility == View.VISIBLE -> {
                showImage(false)
                imgDoneImage.visibility = View.INVISIBLE
                imgDoneImageMain.visibility = View.INVISIBLE
//                imgDoneImageMain1.visibility = View.INVISIBLE
                Constant.lastSelectedPosition = -1
                if(mSelectedImagesUri.isNotEmpty()){
                    imgDoneImageMain.visibility = VISIBLE
                }
                else{
                    imgDoneImageMain.visibility = GONE
                }
            }
            mRVImageVideo.visibility == View.VISIBLE -> {
                showVideo(false)
                imgDoneImage.visibility = View.INVISIBLE
                imgDoneImageMain.visibility = View.INVISIBLE
//                imgDoneImageMain1.visibility = View.INVISIBLE
                Constant.lastSelectedPosition = -1
                Log.d(TAG, "onBackPressed: ${mSelectedImagesUri.size}")
                if(mSelectedImagesUri.isNotEmpty()){
                    imgDoneImageMain.visibility = VISIBLE
//                    imgDoneImageMain1.visibility = VISIBLE
                }
                else{
                    imgDoneImageMain.visibility = GONE
                }
            }


            else -> {
                super.onBackPressed()
                setResult(RESULT_CANCELED)
                for (data in mAlbumListImages){
                    for (data1 in data.mediaUris){
                        data1.isSelected =  false
                    }
                }
                for (data in mAlbumListVideo){
                    for (data1 in data.mediaUris){
                        data1.isSelected =  false
                    }
                }
            }
        }
    }

    private fun setImageAdapter(position: Int, folderName: String) {
        toolbarImage.visibility = View.VISIBLE
        txtFolderName.text = folderName

        showImage(true)
        var layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        layoutManager = GridLayoutManager(this, 4)
        mImageAdapter = ImageAdapter(
            this@ImagePickerActivity,
            if(Constant.buttonColor!=null){Color.parseColor(Constant.buttonColor!!)}else{resources.getColor(R.color.green)},
            mAlbumListImages[position].mediaUris,
            object : ImageAdapter.OnImageClick {
                override fun selectMedia(uri: Media , position1: Int) {
                    if(Constant.isMultipleMode!!){

                        imgDoneImage.visibility = View.VISIBLE
                        imgDoneImageMain.visibility = View.VISIBLE
//                        imgDoneImageMain1.visibility = View.VISIBLE
                        uri.isSelected = !uri.isSelected
                        if(uri.isSelected){
                            mSelectedImagesUri.add(uri.uri)
                        }
                        else{
                            mSelectedImagesUri.remove(uri.uri)
                        }
                        if(mSelectedImagesUri.isEmpty()){
                            imgDoneImage.visibility = View.INVISIBLE
                            imgDoneImageMain.visibility = View.INVISIBLE
                        }
//                        mImageAdapter.notifyDataSetChanged()

                    }
                    else {
                        if (uri.toString().isNotEmpty()) {
                            imgDoneImage.visibility = View.INVISIBLE
                            imgDoneImageMain.visibility = View.INVISIBLE
//                            imgDoneImageMain1.visibility = View.INVISIBLE
                            path = uri.uri
                            if (Constant.isCrop!!) {
                                startCrop(uri.uri)
                            } else {
                                if(Constant.isMultipleMode!!){
                                    mSelectedImagesUri.add(uri.uri)
                                    setResult(RESULT_OK, Intent().putExtra("imageData", mSelectedImagesUri))
                                }
                                else{
                                    setResult(RESULT_OK, Intent().putExtra("data", uri.uri))
                                }
                                finish()
                            }

                        } else {
                            imgDoneImage.visibility = View.INVISIBLE
                            imgDoneImageMain.visibility = View.INVISIBLE
//                            imgDoneImageMain1.visibility = View.INVISIBLE
                            Toast.makeText(
                                this@ImagePickerActivity,
                                "Please Select Image",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                }

            }, object : ImageAdapter.OnCorruptClick {
                override fun onCorruptImageClicked() {
                    builder!!.setMessage("This Image is corrupted. Please select another Image.")
                    builder!!.setPositiveButton("Ok") { dialog, which -> dialog.dismiss() }
                    alert = builder!!.create()
                    alert!!.show()
                }

            } )
        mRVImage.layoutManager = layoutManager
        mRVImage.adapter = mImageAdapter
    }

    private fun startCrop(uri: Uri) {
        val options = CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setGuidelinesColor(Color.WHITE)
            .setBorderCornerColor(Color.WHITE)
            .setBorderLineColor(Color.WHITE)
            .start(this@ImagePickerActivity)
    }



    private fun showImage(b: Boolean) {
        if (b) {
            mRVAlbum.visibility = View.GONE
            toolbar.visibility = View.GONE
            mRVImage.visibility = View.VISIBLE
            toolbarImage.visibility = View.VISIBLE
            mRVAlbumVideo.visibility = GONE
            mRVImageVideo.visibility = GONE
            image.visibility = GONE
            video.visibility = GONE

        } else {
            mRVAlbum.visibility = View.VISIBLE
            toolbar.visibility = View.VISIBLE
            mRVImage.visibility = View.GONE
            toolbarImage.visibility = View.GONE
            mRVAlbumVideo.visibility = GONE
            mRVImageVideo.visibility = GONE
            if (Constant.isCrop!=true && Constant.mediaType==Constant.BOTH) {
                image.visibility = VISIBLE
                video.visibility = VISIBLE
            }
            else{
                image.visibility = GONE
                video.visibility = GONE
            }
        }
    }

    private fun showVideo(b: Boolean) {
        if (b) {
            mRVAlbum.visibility = View.GONE
            toolbar.visibility = View.GONE
            mRVImage.visibility = View.GONE
            toolbarImage.visibility = View.VISIBLE
            mRVAlbumVideo.visibility = GONE
            mRVImageVideo.visibility = VISIBLE
            image.visibility = GONE
            video.visibility = GONE
        } else {
            mRVAlbum.visibility = View.GONE
            toolbar.visibility = View.VISIBLE
            mRVImage.visibility = View.GONE
            toolbarImage.visibility = View.GONE
            mRVAlbumVideo.visibility = VISIBLE
            mRVImageVideo.visibility = GONE
            if (!Constant.isCrop && Constant.mediaType==Constant.BOTH) {
                image.visibility = VISIBLE
                video.visibility = VISIBLE
            }
            else{
                image.visibility = GONE
                video.visibility = GONE
            }
        }
    }



    override fun onPause() {
        super.onPause()
        if (alert != null && alert!!.isShowing) {
            alert!!.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result!!.uri

                Log.d(TAG, "onActivityResult: check the::success:${resultUri.toString()}")
                Toast.makeText(this,"Crop Successfully",Toast.LENGTH_SHORT).show()

                val resultRatio = data!!.getStringExtra("CROP_IMAGE_EXTRA_RATIO")
                val images = resultUri.toString()
                Log.d("TAG", "onActivityResult: $resultUri")
                Log.d("TAG", "onActivityResult: Ratio $resultRatio")
                val intent = Intent()
                intent.putExtra(EXTRA_SELECTED_URI, images)
                intent.putExtra(EXTRA_SELECTED_RATIO, resultRatio)
                Log.d(TAG, "onCreate: $images")
                setResult(RESULT_OK, intent)
                finish()
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result!!.error
                Log.d(TAG, "onActivityResult: check the::error:${error.toString()}")
                Toast.makeText(this@ImagePickerActivity, "Image Crop Fails", Toast.LENGTH_SHORT).show()
            }

    }


}
}

