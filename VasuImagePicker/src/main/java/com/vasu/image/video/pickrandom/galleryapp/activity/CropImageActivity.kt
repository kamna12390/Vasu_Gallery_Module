package com.vasu.image.video.pickrandom.galleryapp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imagecrop.Crop.CropImageView
import com.vasu.image.video.pickrandom.galleryapp.Crop.ImageOptions
import com.vasu.image.video.pickrandom.galleryapp.R
import com.vasu.image.video.pickrandom.galleryapp.adapter.CropAdapter
import com.vasu.image.video.pickrandom.galleryapp.adapter.ShapeAdapter
import com.vasu.image.video.pickrandom.galleryapp.databinding.CropImageActivityBinding
import com.vasu.image.video.pickrandom.galleryapp.databinding.CustomRatioBinding
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant.mCropIconListt
import com.vasu.image.video.pickrandom.galleryapp.model.CropModel
import com.vasu.image.video.pickrandom.galleryapp.util.BitmapUtils
import com.vasu.image.video.pickrandom.galleryapp.util.CropImage
import com.vasu.image.video.pickrandom.galleryapp.util.CropImage.getPickImageResultUri
import com.vasu.image.video.pickrandom.galleryapp.util.CropImage.isExplicitCameraPermissionRequired
import com.vasu.image.video.pickrandom.galleryapp.util.CropImage.isReadExternalStoragePermissionsRequired
import com.vasu.image.video.pickrandom.galleryapp.util.CropImage.startPickImageActivity
import com.vasu.image.video.pickrandom.galleryapp.util.PathParser
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


class CropImageActivity : AppCompatActivity(), CropImageView.OnSetImageUriCompleteListener,
    CropImageView.OnCropImageCompleteListener {
    private lateinit var binding: CropImageActivityBinding
    private var TAG = "Crop Image Activity"
    private var mCropImageUri: Uri? = null
    private var imageoptions: ImageOptions? = null
    private var dialog: Dialog? = null
    private var outputUri: Uri? = null
    var cropadapter: CropAdapter? = null
    var shapeAdapter: ShapeAdapter? = null
    var bitmap: Bitmap? = null
    private var lastClick: Long = 0
    var dialogbinding: CustomRatioBinding? = null

    @SuppressLint("NewApi")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.crop_image_activity)
        binding = CropImageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)




        dialog = Dialog(this, R.style.customdialog)

        setActivityWidget()
        initilistner()
        setcropadpter()
        setshapeadpter()

//        cropImageView!!.setOnSetCropOverlayMovedListener(object : OnSetCropOverlayMovedListener {
//            override fun onCropOverlayMoved(rect: Rect?) {
//                Log.d(TAG, "onCropOverlayMoved: check the data:${rect!!.height()}")
//                Log.d(TAG, "onCropOverlayMoved: check the data:${rect.width()}")
//
//            }
//        })


        val bundle = intent.getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE)
        mCropImageUri = bundle!!.getParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE)
        imageoptions = bundle.getParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS)
        if (savedInstanceState == null) {
            if (mCropImageUri == null || mCropImageUri == Uri.EMPTY) {
                if (isExplicitCameraPermissionRequired(this)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE
                    )
                } else {
                    startPickImageActivity(this)
                }
            } else {
                // no permissions required or already grunted, can start crop image activity
                binding.cropImageView!!.setImageUriAsync(mCropImageUri)
            }
        }

    }

    private fun setActivityWidget() {
        if (Constant.cropIconInCrop != null) {
            binding.imgcrop.setImageDrawable(Constant.cropIconInCrop)
        }
        else{
            binding.imgcrop.setImageResource(R.drawable.ic_crop_test)
        }

        if (Constant.rotateIconInCrop != null) {
            binding.imgrotate.setImageDrawable(Constant.rotateIconInCrop)
        }
        else{
            binding.imgrotate.setImageResource(R.drawable.ic_rotate_test)
        }


        if (Constant.rotateLeftIconInCrop != null) {
            binding.imgleft.setImageDrawable(Constant.rotateLeftIconInCrop)
        }
        else{
            binding.imgleft.setImageResource(R.drawable.ic_left_test)
        }

        if (Constant.backIcon != null) {
            setbackimageDrawacle(Constant.backIcon!!)
        }
        else{
            setbackimage(R.drawable.ic_back_arrow_test_1)
        }
        if (Constant.doneIcon != null) {
            setdoneimagedrawable(Constant.doneIcon!!)
        }
        else{
            setdoneimage(R.drawable.ic_done)
        }


        if(Constant.rotateRightIconInCrop!=null){
            binding.imgright.setImageDrawable(Constant.rotateRightIconInCrop)
        }
        else{
            binding.imgright.setImageResource(R.drawable.ic_right_test)
        }

        if(Constant.flipHorizontal!=null) {
            binding.imgHorizontal.setImageDrawable(Constant.flipHorizontal)
        }
        else{
            binding.imgHorizontal.setImageResource(R.drawable.ic_flip_horizontal_test)
        }

        if(Constant.flipVertical != null){
            binding.imgVertical.setImageDrawable(Constant.flipVertical)
        }
        else{
            binding.imgVertical.setImageResource(R.drawable.ic_flip_vertical_test)
        }
//        if(Constant != null) {
        if (Constant.statusBarColor != null) {
            setstatusbarcolor(Color.parseColor(Constant.statusBarColor!!))
            setnavigationcolor(Color.parseColor(Constant.statusBarColor!!))
        } else {
            setstatusbarcolorRes(R.color.grey)
            setnavigationcolorRes(R.color.grey)
        }
        if (Constant.toolbarIconColor != null) {
            setIconTint(Color.parseColor(Constant.toolbarIconColor!!))
        }
        if (Constant?.toolbarTextColor != null) {
            binding.view.setBackgroundColor(Color.parseColor(Constant.toolbarTextColor))
            settitlecolor(Color.parseColor(Constant.toolbarTextColor!!))
            setrotatebuttontextbackground(Color.parseColor(Constant.toolbarTextColor!!))
            setrotateimagebackground(Color.parseColor(Constant.toolbarTextColor!!))
            setshapebuttontextbackground(Color.parseColor(Constant.toolbarTextColor!!))
            setshapeimagebackground(Color.parseColor(Constant.toolbarTextColor!!))
            binding.imgHorizontal.imageTintList = ColorStateList.valueOf(Color.parseColor(Constant.toolbarTextColor!!))
            binding.imgright.imageTintList = ColorStateList.valueOf(Color.parseColor(Constant.toolbarTextColor!!))
            binding.imgleft.imageTintList = ColorStateList.valueOf(Color.parseColor(Constant.toolbarTextColor!!))
            binding.imgVertical.imageTintList = ColorStateList.valueOf(Color.parseColor(Constant.toolbarTextColor!!))
        } else {
            settitlecolor(Color.WHITE)
            setrotatebuttontextbackground(Color.WHITE)
            setrotateimagebackgroundRes(R.color.white)
            setshapebuttontextbackground(Color.WHITE)
            setshapeimagebackgroundRes(R.color.white)
            binding.imgHorizontal.imageTintList = resources.getColorStateList(R.color.white)
            binding.imgright.imageTintList = resources.getColorStateList(R.color.white)
            binding.imgleft.imageTintList = resources.getColorStateList(R.color.white)
            binding.imgVertical.imageTintList = resources.getColorStateList(R.color.white)
        }

        if (Constant.toolbarColor != null) {
            settoolbarcolor(Color.parseColor(Constant.toolbarColor!!))
            setbottomlayoutbackground(Color.parseColor(Constant.toolbarColor!!))
        } else {
            settoolbarcolorRes(R.color.grey)
            setbottomlayoutbackgroundRes(R.color.grey)
        }

        if (Constant.backgroundColor != null) {
            setimagebackgroundcolor(Color.parseColor(Constant.backgroundColor!!))
        } else {
            setimagebackgroundcolorRes(R.color.darkblack)
        }

        if (Constant.buttonColor != null) {
            setcropbuttontextbackground(Color.parseColor(Constant.buttonColor!!))
            setcropimagetextbackground(Color.parseColor(Constant.buttonColor!!))
        } else {
            setcropbuttontextbackground(Color.parseColor("#35b18e"))
            setcropimagetextbackgroundRes(R.color.green)
        }



        settitle("Crop Image")
//        if(Constant.backIcon!=null){

//        }
//        else{
//            setbackimage(R.drawable.ic_back)
//        }
//       if(Constant.doneIcon!=null){

//       }
//        else{
//           setdoneimage(R.drawable.ic_baseline_done_24)
//       }


    }


    //Method of creating mask runtime
    fun makeMaskImage(mImageView: CropImageView, mContent: Int) {
        val mask = convertToCircle(bitmap!!)
        val original = BitmapFactory.decodeResource(resources, mContent)
//        val mask = BitmapFactory.decodeResource(resources, R.drawable.mask)
        val result = Bitmap.createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)
        val mCanvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        mCanvas.drawBitmap(original, 0f, 0f, null)
        mCanvas.drawBitmap(mask, 0f, 0f, paint)
        paint.xfermode = null
        mImageView.setImageBitmap(result)
        mImageView.scaleType = CropImageView.ScaleType.CENTER
    }

    fun setcropadpter() {
//        val list: MutableList<CropModel> = ArrayList()
//        list.add(
//            CropModel(
//                (if (Constant.freeDrawable != null) {
//                    Constant.freeDrawable!!
//                } else {
//                    AppCompatResources.getDrawable(this, R.drawable.ic_free_test)
//                })!!, "Free"
//            )
//        )
//        list.add(
//            CropModel(
//                (if (Constant.customCropDrawable != null) {
//                    Constant.customCropDrawable!!
//                } else {
//                    AppCompatResources.getDrawable(this, R.drawable.ic_custom)
//                })!!, "Custom"
//            )
//        )
//        list.add(CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_11)!!, "1:1"))
//        list.add(CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_23)!!, "2:3"))
//        list.add(CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_32)!!, "3:2"))
//        list.add(CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_34)!!, "3:4"))
//        list.add(CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_13)!!, "1:3"))
//        list.add(CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_43)!!, "4:3"))
//        list.add(CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_21)!!, "2:1"))
//        list.add(CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_26)!!, "2:6"))
//        list.add(CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_916)!!, "9:16"))
//        list.add(CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_45)!!, "4:5"))
//        list.add(CropModel(AppCompatResources.getDrawable(this, R.drawable.ic_169)!!, "16:9"))
        val textColor =
            if (Constant?.folderTitleColor != null) Color.parseColor(Constant.folderTitleColor) else Color.WHITE
        val bgColor =
            if (Constant.buttonColor != null) Color.parseColor(Constant?.buttonColor) else resources.getColor(
                R.color.green
            )
        val unSelectedColor =
            if (Constant?.folderTitleColor != null) Color.parseColor(Constant.folderTitleColor) else Color.WHITE

        cropadapter = CropAdapter(
            application,
            bgColor,
            mCropIconListt!!,
            object : CropAdapter.CellClickListener {
                override fun onCellClickListener(
                    position: Int,
                    aspectRatioX: Int,
                    aspectRatioY: Int
                ) {
                    Log.d("TAG", "setAdapter: position check ::$position")
                    if (position == 0) {
                        binding.cropImageView!!.clearAspectRatio()
                    } else if (position == 1) {
                        Log.d(
                            "TAG",
                            "setAdapter: fsbgvebherwhrwhjy:::" + binding.cropImageView!!.aspectRatio.first
                        )
                        showdailog()
                    } else if (position >= 2) {
                        binding.cropImageView!!.setAspectRatio(aspectRatioX, aspectRatioY)
                    }
//                    else if (position == 3) {
//                        binding.cropImageView!!.setAspectRatio(2, 3)
//                    } else if (position == 4) {
//                        binding.cropImageView!!.setAspectRatio(3, 2)
//                    } else if (position == 5) {
//                        binding.cropImageView!!.setAspectRatio(3, 4)
//                    } else if (position == 6) {
//                        binding.cropImageView!!.setAspectRatio(1, 3)
//                    } else if (position == 7) {
//                        binding.cropImageView!!.setAspectRatio(4, 3)
//                    } else if (position == 8) {
//                        binding.cropImageView!!.setAspectRatio(2, 1)
//                    } else if (position == 9) {
//                        binding.cropImageView!!.setAspectRatio(2, 6)
//                    } else if (position == 10) {
//                        binding.cropImageView!!.setAspectRatio(9, 16)
//                    } else if (position == 11) {
//                        binding.cropImageView!!.setAspectRatio(4, 5)
//                    } else if (position == 12) {
//                        binding.cropImageView!!.setAspectRatio(16, 9)
//                    }
                }

            })
        binding.RvCrop.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.RvCrop.adapter = cropadapter

    }

    private fun showdailog() {

        dialogbinding = CustomRatioBinding.inflate(layoutInflater);
        dialog!!.setContentView(dialogbinding!!.root);

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog!!.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER

        dialog!!.window!!.attributes = lp

        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(false)

        with(dialogbinding) {
            this?.btnCancel!!.setOnClickListener {
                dialog!!.dismiss()
                cropadapter?.notifyDataSetChanged()
            }

//            ClTop
            if (Constant != null) {
                val bgDrawable =
                    ContextCompat.getDrawable(this@CropImageActivity, R.drawable.ic_bg_dailog_1)
                bgDrawable?.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
                if (Constant.toolbarColor != null) {
                    ClTop.background?.setColorFilter(
                        Color.parseColor(Constant.toolbarColor!!),
                        PorterDuff.Mode.SCREEN
                    )
                }
                ClTop.background = bgDrawable
                if (Constant.toolbarTextColor != null) {
                    btnSet.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                    TVCustom.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                    Tvcustomratio.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                    etX.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                    etX.setHintTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                    etY.setHintTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                    TVHeight.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                    etY.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                }

            }


            btnSet.backgroundTintList =
                if (Constant.buttonColor != null) ColorStateList.valueOf(Color.parseColor(Constant.buttonColor!!)) else resources.getColorStateList(
                    R.color.green
                )

            btnSet.setOnClickListener {
                if (etY.text.toString().isEmpty() || etX.text.toString().isEmpty()) {
                    Toast.makeText(applicationContext, "Enter value", Toast.LENGTH_SHORT).show()
                }
                else {
                    val newwidth = etX.text.toString().toInt()
                    val newheight = etY.text.toString().toInt()
                    if (newwidth == 0 || newheight == 0) {
                        Toast.makeText(
                            applicationContext,
                            "Enter Greater 0 Value",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        val otherhight = etY.text.toString().toFloat()
                        val otherwidth = etX.text.toString().toFloat()

                        val ratio = otherwidth / otherhight
                        Log.d("TAG", "onCellClickListener: check the ratio::$ratio")
                        if (ratio < 0.1 || ratio > 20) {
                            Toast.makeText(
                                this@CropImageActivity,
                                "Enter Valid Ratio",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.cropImageView!!.clearAspectRatio()
                        } else {
                            binding.cropImageView!!.setAspectRatio(newwidth, newheight)

                            Log.d(
                                "TAG",
                                "onCellClickListener: get aspect ratio::${binding.cropImageView?.aspectRatio}"
                            )
                        }

                        Log.d(
                            "TAG",
                            "setAdapter: mcropfwgbsf" + binding.cropImageView!!.aspectRatio.first
                        )
                        Log.d(
                            "TAG",
                            "setAdapter: mcropfwgbsf" + binding.cropImageView!!.aspectRatio.second
                        )
                        Log.d(
                            "TAG",
                            "setAdapter: mcropfwgbsf" + binding.cropImageView!!.aspectRatio
                        )

                        dialog!!.dismiss()
                        cropadapter?.notifyDataSetChanged()

                    }

                }
            }

        }



        dialog?.show()

    }


    fun setshapeadpter() {

        val list: MutableList<CropModel> = ArrayList()


        list.add(
            CropModel(
                if(Constant.flipHorizontal!=null){Constant.flipHorizontal}else{AppCompatResources.getDrawable(this, R.drawable.ic_flip_horizontal_test)!!},
                "Square" , 1 ,1
            )
        )
        list.add(
            CropModel(
                if(Constant.flipHorizontal!=null){Constant.flipHorizontal}else{AppCompatResources.getDrawable(this, R.drawable.ic_flip_horizontal_test)!!},
                "Circle" , 1 ,1
            )
        )
        list.add(
            CropModel(
                if(Constant.flipHorizontal!=null){Constant.flipHorizontal}else{AppCompatResources.getDrawable(this, R.drawable.ic_flip_horizontal_test)!!},
                "Roundrect" , 1 ,1
            )
        )
        list.add(
            CropModel(
                if(Constant.flipHorizontal!=null){Constant.flipHorizontal}else{AppCompatResources.getDrawable(this, R.drawable.ic_flip_horizontal_test)!!},
                "Hexagone" , 1 ,1
            )
        )
        list.add(
            CropModel(
                if(Constant.flipHorizontal!=null){Constant.flipHorizontal}else{AppCompatResources.getDrawable(this, R.drawable.ic_flip_horizontal_test)!!},
                "Star" , 1 ,1
            )
        )
        list.add(
            CropModel(
                if(Constant.flipHorizontal!=null){Constant.flipHorizontal}else{AppCompatResources.getDrawable(this, R.drawable.ic_flip_horizontal_test)!!},
                "Triangle" , 1 ,1
            )
        )


        binding.RvShape.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        val textColor =
            if (Constant.folderTitleColor != null) Color.parseColor(Constant.folderTitleColor) else Color.WHITE
        val bgColor =
            if (Constant.buttonColor != null) Color.parseColor(Constant.buttonColor) else resources.getColor(
                R.color.green
            )
        val unSelectedColor =
            if (Constant.folderTitleColor != null) Color.parseColor(Constant.folderTitleColor) else Color.WHITE
        shapeAdapter = ShapeAdapter(
            application,
            textColor,
            bgColor,
            unSelectedColor,
            list,
            object : ShapeAdapter.CellClickListener {

                override fun onCellClickListener(position: Int) {

                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, mCropImageUri)

                    binding.cropImageView!!.setAspectRatio(1, 1)
                    if (position == 0) {
                        binding.cropImageView?.cropShape = CropImageView.CropShape.RECTANGLE

                    } else if (position == 1) {

                        binding.cropImageView?.cropShape = CropImageView.CropShape.OVAL
                        binding.cropImageView!!.setImageBitmap(convertToCircle(bitmap!!))

                    } else if (position == 2) {
                        binding.cropImageView?.cropShape = CropImageView.CropShape.ROUNDRECT
                    } else if (position == 3) {
                        binding.cropImageView?.cropShape = CropImageView.CropShape.HEXAGONE

                    } else if (position == 4) {
                        binding.cropImageView?.cropShape = CropImageView.CropShape.STAR

                    } else if (position == 5) {
                        binding.cropImageView?.cropShape = CropImageView.CropShape.TRIANGLE

                    }
                }

            })
        binding.RvShape.adapter = shapeAdapter

    }


    private fun convertToHeart(src: Bitmap): Bitmap {
        return BitmapUtils.getCroppedBitmap(src, getHeartPath(src))
    }

    private fun convertToCircle(src: Bitmap): Bitmap {
        return BitmapUtils.getCroppedBitmap(src, getCirclePath(src))
    }

    private fun convertToHexagone(src: Bitmap): Bitmap {
        return BitmapUtils.getCroppedBitmap(src, getHexagonePath(src))
    }

    private fun convertToHive(src: Bitmap): Bitmap {
        return BitmapUtils.getCroppedBitmap(src, getHivePath(src))
    }

    private fun convertToStar(src: Bitmap): Bitmap {
        return BitmapUtils.getCroppedBitmap(src, getStarPath(src))
    }

    private fun convertToCloud(src: Bitmap): Bitmap {
        return BitmapUtils.getCroppedBitmap(src, getCloudPath(src))
    }

    private fun getHeartPath(src: Bitmap): Path {
        return resizePath(
            PathParser.createPathFromPathData(getString(R.string.heart)),
            src.width.toFloat(), src.height.toFloat()
        )
    }

    private fun getCirclePath(src: Bitmap): Path {
        return resizePath(
            PathParser.createPathFromPathData(getString(R.string.circle)),
            src.width.toFloat(), src.height.toFloat()
        )
    }

    private fun getHexagonePath(src: Bitmap): Path {
        return resizePath(
            PathParser.createPathFromPathData(getString(R.string.hexagone)),
            src.width.toFloat(), src.height.toFloat()
        )
    }

    private fun getHivePath(src: Bitmap): Path {
        return resizePath(
            PathParser.createPathFromPathData(getString(R.string.hive)),
            src.width.toFloat(), src.height.toFloat()
        )
    }

    private fun getStarPath(src: Bitmap): Path {
        return resizePath(
            PathParser.createPathFromPathData(getString(R.string.star)),
            src.width.toFloat(), src.height.toFloat()
        )
    }

    private fun getCloudPath(src: Bitmap): Path {
        return resizePath(
            PathParser.createPathFromPathData(getString(R.string.cloud)),
            src.width.toFloat(), src.height.toFloat()
        )
    }

    fun resizePath(path: Path, width: Float, height: Float): Path {
        val bounds = RectF(0f, 0f, width, height)
        val resizedPath = Path(path)
        val src = RectF()
        resizedPath.computeBounds(src, true)
        val resizeMatrix = Matrix()
        resizeMatrix.setRectToRect(src, bounds, Matrix.ScaleToFit.CENTER)
        resizedPath.transform(resizeMatrix)
        return resizedPath
    }


    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun initilistner() {

        with(binding) {

            ClBottomCrop.setOnClickListener {
                ClRotate.visibility = View.GONE
                ClCrop.visibility = View.VISIBLE
                ClShape.visibility = View.GONE
                tvcrop.setTextColor(
                    if (Constant.buttonColor != null) Color.parseColor(Constant.buttonColor!!) else Color.parseColor(
                        "#35b18e"
                    )
                )
                tvrotate.setTextColor(
                    if (Constant.toolbarTextColor != null) Color.parseColor(
                        Constant.toolbarTextColor!!
                    ) else Color.WHITE
                )
                tvshape.setTextColor(
                    if (Constant.toolbarTextColor != null) Color.parseColor(
                        Constant.toolbarTextColor!!
                    ) else Color.WHITE
                )
                imgcrop.imageTintList = if (Constant.buttonColor != null) ColorStateList.valueOf(
                    Color.parseColor(Constant.buttonColor!!)
                ) else resources.getColorStateList(R.color.green)
                imgrotate.imageTintList =
                    if (Constant.toolbarTextColor != null) ColorStateList.valueOf(
                        Color.parseColor(
                            Constant.toolbarTextColor!!
                        )
                    ) else resources.getColorStateList(
                        R.color.white
                    )
                imgshape.backgroundTintList =
                    if (Constant.toolbarTextColor != null) ColorStateList.valueOf(
                        Color.parseColor(
                            Constant.toolbarTextColor!!
                        )
                    ) else resources.getColorStateList(
                        R.color.white
                    )

            }
            ClBottomRotate.setOnClickListener {
                ClRotate.visibility = View.VISIBLE
                ClCrop.visibility = View.GONE
                ClShape.visibility = View.GONE
                tvrotate.setTextColor(
                    if (Constant.buttonColor != null) Color.parseColor(Constant.buttonColor!!) else Color.parseColor(
                        "#35b18e"
                    )
                )
                tvcrop.setTextColor(if (Constant.toolbarTextColor != null) Color.parseColor(Constant.toolbarTextColor!!) else Color.WHITE)
                tvshape.setTextColor(
                    if (Constant.toolbarTextColor != null) Color.parseColor(
                        Constant.toolbarTextColor!!
                    ) else Color.WHITE
                )
                imgrotate.imageTintList =
                    if (Constant.buttonColor != null) ColorStateList.valueOf(
                        Color.parseColor(
                            Constant.buttonColor!!
                        )
                    ) else resources.getColorStateList(
                        R.color.green
                    )
                imgcrop.imageTintList =
                    if (Constant.toolbarTextColor != null) ColorStateList.valueOf(
                        Color.parseColor(
                            Constant.toolbarTextColor!!
                        )
                    ) else resources.getColorStateList(
                        R.color.white
                    )
                imgshape.backgroundTintList =
                    if (Constant.toolbarTextColor != null) ColorStateList.valueOf(
                        Color.parseColor(
                            Constant.toolbarTextColor!!
                        )
                    ) else resources.getColorStateList(
                        R.color.white
                    )

            }
            ClBottomShape.setOnClickListener {
//            ClRotate!!.visibility = View.GONE
//            ClCrop!!.visibility = View.GONE
//            ClShape!!.visibility = View.VISIBLE
//            txtrotate!!.setTextColor(Color.WHITE)
//            txtcrop!!.setTextColor(Color.WHITE)
//            tvshape!!.setTextColor(Color.parseColor("#35b18e"))
//            imgrotate?.backgroundTintList = resources.getColorStateList(R.color.white)
//            imgcrop?.backgroundTintList = resources.getColorStateList(R.color.white)
//            imgshape?.backgroundTintList = resources.getColorStateList(R.color.green)
            }
            clleft.setOnClickListener {
                rotateImage(-imageoptions!!.rotationDegrees)
                if (Constant.buttonColor != null) {
                    txtleft.setTextColor(Color.parseColor(Constant.buttonColor!!))
                    imgleft.setColorFilter(Color.parseColor(Constant.buttonColor!!))
                } else {
                    txtleft.setTextColor(Color.parseColor("#35b18e"))
                    imgleft.setColorFilter(Color.parseColor("#35b18e"))
                }
                if (Constant.toolbarTextColor != null) {
                    imgright.setColorFilter(Color.parseColor(Constant.toolbarTextColor!!))
                    imgHorizontal.setColorFilter(Color.parseColor(Constant.toolbarTextColor!!))
                    imgVertical.setColorFilter(Color.parseColor(Constant.toolbarTextColor!!))
                    txtright.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                    txtVertical.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                    txtHorizontal.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                } else {
                    imgright.setColorFilter(Color.WHITE)
                    imgHorizontal.setColorFilter(Color.WHITE)
                    imgVertical.setColorFilter(Color.WHITE)
                    txtright.setTextColor(Color.WHITE)
                    txtVertical.setTextColor(Color.WHITE)
                    txtHorizontal.setTextColor(Color.WHITE)
                }


            }
            clright.setOnClickListener {
                rotateImage(imageoptions!!.rotationDegrees)

                if (Constant.buttonColor != null) {
                    txtright.setTextColor(Color.parseColor(Constant.buttonColor!!))
                    imgright.setColorFilter(Color.parseColor(Constant.buttonColor!!))
                } else {
                    txtright.setTextColor(Color.parseColor("#35b18e"))
                    imgright.setColorFilter(Color.parseColor("#35b18e"))
                }
                if (Constant.toolbarTextColor != null) {
                    imgleft.setColorFilter(Color.parseColor(Constant.toolbarTextColor!!))
                    imgHorizontal.setColorFilter(Color.parseColor(Constant.toolbarTextColor!!))
                    imgVertical.setColorFilter(Color.parseColor(Constant.toolbarTextColor!!))
                    txtleft.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                    txtVertical.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                    txtHorizontal.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                } else {
                    imgleft.setColorFilter(Color.WHITE)
                    imgHorizontal.setColorFilter(Color.WHITE)
                    imgVertical.setColorFilter(Color.WHITE)
                    txtleft.setTextColor(Color.WHITE)
                    txtVertical.setTextColor(Color.WHITE)
                    txtHorizontal.setTextColor(Color.WHITE)
                }

            }
            clflipVertical.setOnClickListener {
                cropImageView.flipImageVertically()

                if (Constant.buttonColor != null) {
                    txtVertical.setTextColor(Color.parseColor(Constant.buttonColor!!))
                    imgVertical.setColorFilter(Color.parseColor(Constant.buttonColor!!))
                } else {
                    txtVertical.setTextColor(Color.parseColor("#35b18e"))
                    imgVertical.setColorFilter(Color.parseColor("#35b18e"))
                }
                if (Constant.toolbarTextColor != null) {
                    imgleft.setColorFilter(Color.parseColor(Constant.toolbarTextColor!!))
                    imgHorizontal.setColorFilter(Color.parseColor(Constant.toolbarTextColor!!))
                    imgright.setColorFilter(Color.parseColor(Constant.toolbarTextColor!!))
                    txtleft.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                    txtright.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                    txtHorizontal.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                } else {
                    imgleft.setColorFilter(Color.WHITE)
                    imgHorizontal.setColorFilter(Color.WHITE)
                    imgright.setColorFilter(Color.WHITE)
                    txtleft.setTextColor(Color.WHITE)
                    txtright.setTextColor(Color.WHITE)
                    txtHorizontal.setTextColor(Color.WHITE)
                }
            }
            clHorizontal.setOnClickListener {
                cropImageView.flipImageHorizontally()
                if (Constant.buttonColor != null) {
                    txtHorizontal.setTextColor(Color.parseColor(Constant.buttonColor!!))
                    imgHorizontal.setColorFilter(Color.parseColor(Constant.buttonColor!!))
                } else {
                    txtHorizontal.setTextColor(Color.parseColor("#35b18e"))
                    imgHorizontal.setColorFilter(Color.parseColor("#35b18e"))
                }
                if (Constant.toolbarTextColor != null) {
                    imgleft.setColorFilter(Color.parseColor(Constant.toolbarTextColor!!))
                    imgVertical.setColorFilter(Color.parseColor(Constant.toolbarTextColor!!))
                    imgright.setColorFilter(Color.parseColor(Constant.toolbarTextColor!!))
                    txtleft.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                    txtright.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                    txtVertical.setTextColor(Color.parseColor(Constant.toolbarTextColor!!))
                } else {
                    imgleft.setColorFilter(Color.WHITE)
                    imgright.setColorFilter(Color.WHITE)
                    imgVertical.setColorFilter(Color.WHITE)
                    txtleft.setTextColor(Color.WHITE)
                    txtVertical.setTextColor(Color.WHITE)
                    txtright.setTextColor(Color.WHITE)
                }

            }
            imgBtnSaved.setOnClickListener {
                if (SystemClock.elapsedRealtime() - lastClick < 2000) {
                    return@setOnClickListener
                }
                lastClick = SystemClock.elapsedRealtime()

                cropImage()

            }
            imgBack.setOnClickListener {
                if (SystemClock.elapsedRealtime() - lastClick < 2000) {
                    return@setOnClickListener
                }
                lastClick = SystemClock.elapsedRealtime()
                setResultCancel()

            }


        }
    }


    override fun onStart() {
        super.onStart()
        binding.cropImageView!!.setOnSetImageUriCompleteListener(this)
        binding.cropImageView!!.setOnCropImageCompleteListener(this)
    }

    override fun onStop() {
        super.onStop()
        binding.cropImageView!!.setOnSetImageUriCompleteListener(null)
        binding.cropImageView!!.setOnCropImageCompleteListener(null)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        setResultCancel()
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                setResultCancel()
            }
            if (resultCode == RESULT_OK) {
                mCropImageUri = getPickImageResultUri(this, data)

                Log.d("TAG", "onActivityResult: check the data::$mCropImageUri")
                if (isReadExternalStoragePermissionsRequired(this, mCropImageUri!!)) {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE
                    )
                } else {
                    binding.cropImageView!!.setImageUriAsync(mCropImageUri)

                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.cropImageView!!.setImageUriAsync(mCropImageUri)
            } else {
                setResultCancel()
            }
        }
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            startPickImageActivity(this)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return false
    }

    override fun onSetImageUriComplete(view: CropImageView?, uri: Uri?, error: Exception?) {
        if (error == null) {
            if (imageoptions!!.initialCropWindowRectangle != null) {
                binding.cropImageView!!.cropRect = imageoptions!!.initialCropWindowRectangle
            }
            if (imageoptions!!.initialRotation > -1) {
                binding.cropImageView!!.rotatedDegrees = imageoptions!!.initialRotation
            }

        } else {
            setResult(null, error, 1)
            Log.d("TAG", "setResult: check data::vwfbveqwfgbegb::::$uri")
        }
    }

    override fun onCropImageComplete(view: CropImageView?, result: CropImageView.CropResult?) {
        Log.d("TAG", "onCropImageComplete: data chek::${view!!.croppedImage!!.width}")
        setResult(result!!.uri, result.error, result.sampleSize)
    }


    @OptIn(DelicateCoroutinesApi::class)
    fun cropImage() {

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                Log.d(TAG, "initilistner: chheck the scope:::io")
                outputUri = getOutputUri()

            }
            withContext(Dispatchers.Main) {

                if (imageoptions!!.noOutputImage) {
                    setResult(null, null, 1)
                } else {

                    Log.d(TAG, "initilistner: chheck the scope:::main")
                    Log.d(TAG, "cropImage: check the uri:${outputUri?.path}")

                    val format = imageoptions!!.outputCompressFormat
                    val quality = imageoptions!!.outputCompressQuality
                    val width = imageoptions!!.outputRequestWidth
                    val height = imageoptions!!.outputRequestHeight
                    val options = imageoptions!!.outputRequestSizeOptions
                    val flipVertical = imageoptions!!.flipVertically
                    val flipHorizontal = imageoptions!!.flipHorizontally

                    val check = binding.cropImageView?.saveCroppedImageAsync(
                        outputUri,
                        format,
                        quality,
                        width,
                        height,
                        options,
                        flipVertical,
                        flipHorizontal
                    )

                    Log.d(TAG, "cropImage: check the images  <----------> :${check}")


                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (outputUri != null) {
            val format = imageoptions!!.outputCompressFormat
            val quality = imageoptions!!.outputCompressQuality
            val width = imageoptions!!.outputRequestWidth
            val height = imageoptions!!.outputRequestHeight
            val options = imageoptions!!.outputRequestSizeOptions
            val flipVertical = imageoptions!!.flipVertically
            val flipHorizontal = imageoptions!!.flipHorizontally

            binding.cropImageView?.saveCroppedImageAsync(
                outputUri,
                format,
                quality,
                width,
                height,
                options,
                flipVertical,
                flipHorizontal
            )
        }
    }


    fun getCircularBitmap(bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) {
            return null
        }
        val createBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val canvas = Canvas(createBitmap)
        val width = bitmap.width / 2
        val height = bitmap.height / 2
        val paint = Paint()
        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        canvas.drawCircle(
            width.toFloat(),
            height.toFloat(),
            Math.min(width, height).toFloat(),
            paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return createBitmap
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }


    fun rotateImage(degrees: Int) {
        binding.cropImageView!!.rotateImage(degrees)
    }

    @SuppressLint("SuspiciousIndentation")
    fun getOutputUri(): Uri? {
        var outputUri = imageoptions!!.outputUri
        if (outputUri == null || outputUri == Uri.EMPTY) {
            outputUri = try {
                val ext =
                    if (imageoptions!!.outputCompressFormat === Bitmap.CompressFormat.JPEG) ".jpg" else if (imageoptions!!.outputCompressFormat === Bitmap.CompressFormat.PNG) ".png" else ".webp"
                Uri.fromFile(File.createTempFile("cropped", ext, cacheDir))


            } catch (e: IOException) {
                throw RuntimeException("Failed to create temp file for output image", e)
            }


        }

        Log.d("TAG", "getResultIntent: chec rfge2bgeb_+dsfadfadf::2:${outputUri?.path}")
        Log.d("TAG", "getResultIntent: chec rfge2bgeb_+dsfadfadf::2:${imageoptions!!.cropShape}")

        return outputUri
    }

    fun setResult(uri: Uri?, error: Exception?, sampleSize: Int) {
        val resultCode =
            if (error == null) RESULT_OK
            else
                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE

        setResult(resultCode, getResultIntent(uri, error, sampleSize))
        finish()
    }

    fun setResultCancel() {
        setResult(RESULT_CANCELED)
        finish()
    }

    fun getResultIntent(uri: Uri?, error: Exception?, sampleSize: Int): Intent {

        val result = CropImage.ActivityResult(
            binding.cropImageView!!.imageUri,
            uri,
            error,
            binding.cropImageView!!.cropPoints,
            binding.cropImageView!!.cropRect,
            binding.cropImageView!!.rotatedDegrees,
            binding.cropImageView!!.wholeImageRect,
            sampleSize,
        )


        Log.d(TAG, "getResultIntent: crop shape::${binding.cropImageView!!.imageUri}")
        Log.d(TAG, "getResultIntent: crop shape::${uri}")
        Log.d(TAG, "getResultIntent: crop shape::${error}")
        Log.d(TAG, "getResultIntent: crop shape::${binding.cropImageView!!.cropRect!!.height()}")
        Log.d(TAG, "getResultIntent: crop shape::${binding.cropImageView!!.cropRect!!.width()}")
        Log.d(TAG, "getResultIntent: crop shape::${binding.cropImageView!!.rotatedDegrees}")
        Log.d(
            TAG,
            "getResultIntent: crop shape::${binding.cropImageView!!.wholeImageRect!!.height()}"
        )
        Log.d(
            TAG,
            "getResultIntent: crop shape::${binding.cropImageView!!.wholeImageRect!!.width()}"
        )

        val intent = Intent()
        intent.putExtras(getIntent())
        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, result)

        Log.d("TAG", "getResultIntent: chec rfge2bgeb_+$uri")
        return intent
    }

    fun updateMenuItemIconColor(menu: Menu, itemId: Int, color: Int) {
        val menuItem = menu.findItem(itemId)
        if (menuItem != null) {
            val menuItemIcon = menuItem.icon
            if (menuItemIcon != null) {
                try {
                    menuItemIcon.mutate()
                    menuItemIcon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                    menuItem.icon = menuItemIcon
                } catch (e: Exception) {
                    Log.w("AIC", "Failed to update menu item color", e)
                }
            }
        }
    } // endregion


    companion object {

        val CREATOR: Parcelable.Creator<ImageOptions?> =
            object : Parcelable.Creator<ImageOptions?> {
                override fun createFromParcel(parcel: Parcel): ImageOptions {
                    return ImageOptions(parcel)
                }

                override fun newArray(size: Int): Array<ImageOptions?> {
                    return arrayOfNulls(size)
                }
            }
    }


    fun setbackimage(drawable: Int) {
        binding.imgBack.setImageResource(drawable)
    }

    fun setbackimageDrawacle(drawable: Drawable) {
        binding.imgBack.setImageDrawable(drawable)
    }

    fun setIconTint(color: Int) {
        binding.imgBack.setColorFilter(color)
        binding.imgBtnSaved.setColorFilter(color)
    }

    fun setdoneimage(drawable: Int) {
        binding.imgBtnSaved.setImageResource(drawable)
    }

    fun setdoneimagedrawable(drawable: Drawable) {
        binding.imgBtnSaved.setImageDrawable(drawable)
    }

    fun settitle(string: String) {
        binding.tvtitle.text = string
    }

    fun settitlecolor(color: Int) {
        binding.tvtitle.setTextColor(color)
    }

    fun setstatusbarcolor(color: Int) {
        window.statusBarColor = color
    }

    fun setstatusbarcolorRes(color: Int) {
        window.statusBarColor = ContextCompat.getColor(this, color)
    }

    fun setnavigationcolor(color: Int) {
        window.navigationBarColor = color

    }

    fun setnavigationcolorRes(color: Int) {
        window.navigationBarColor = ContextCompat.getColor(this, color)

    }

    fun settoolbarcolor(color: Int) {
        binding.llToolbar.setBackgroundColor(color)
    }

    fun settoolbarcolorRes(color: Int) {
        binding.llToolbar.setBackgroundResource(color)
    }

    fun setimagebackgroundcolor(color: Int) {
        binding.cltop.setBackgroundColor(color)
    }

    fun setimagebackgroundcolorRes(color: Int) {
        binding.cltop.setBackgroundResource(color)
    }

    fun setbottomlayoutbackground(color: Int) {
        binding.clbottom.setBackgroundColor(color)
    }

    fun setbottomlayoutbackgroundRes(color: Int) {
        binding.clbottom.setBackgroundResource(color)
    }

    fun setcropbuttontextbackground(color: Int) {
        binding.tvcrop.setTextColor(color)
    }

    fun setcropimagetextbackground(color: Int) {
        binding.imgcrop.imageTintList = ColorStateList.valueOf(color)
    }

    fun setcropimagetextbackgroundRes(color: Int) {
        binding.imgcrop.imageTintList = resources.getColorStateList(color)
    }

    fun setrotatebuttontextbackground(color1: Int) {
        binding.tvrotate.setTextColor(color1)
    }

    fun setrotateimagebackground(color: Int) {
        binding.imgrotate.imageTintList = ColorStateList.valueOf(color)

    }

    fun setrotateimagebackgroundRes(color: Int) {
        binding.imgrotate.imageTintList = resources.getColorStateList(color)

    }

    fun setshapebuttontextbackground(color1: Int) {
        binding.tvshape.setTextColor(color1)
    }

    fun setshapeimagebackground(color: Int) {
        binding.imgshape.imageTintList = ColorStateList.valueOf(color)

    }

    fun setshapeimagebackgroundRes(color: Int) {
        binding.imgshape.imageTintList = resources.getColorStateList(color)

    }


}