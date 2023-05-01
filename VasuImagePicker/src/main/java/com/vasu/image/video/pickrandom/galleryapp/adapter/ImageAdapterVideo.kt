package com.vasu.image.video.pickrandom.galleryapp.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.vasu.image.video.pickrandom.galleryapp.R
import com.vasu.image.video.pickrandom.galleryapp.activity.ImagePickerActivity
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant
import com.vasu.image.video.pickrandom.galleryapp.model.Media

class ImageAdapterVideo (
    var mContext: ImagePickerActivity,
    var color : Int,
    var mediaUris: List<Media>,
    var listener: OnImageClick,
    var mCorrupt : OnCorruptClick,
) :
    RecyclerView.Adapter<ImageAdapterVideo.ViewHolder>() {



    private var lastPosition = -1

    interface OnImageClick {
        fun selectMedia(uri: Media)
    }
    interface OnCorruptClick{
        fun onCorruptImageClicked()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mImage = itemView.findViewById<ImageView>(R.id.ivImage)!!
        var mSelectImage = itemView.findViewById<ImageView>(R.id.ivSelectImage)!!
        var overlay = itemView.findViewById<View>(R.id.overlay)!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("TAG", "onCreateViewHolder: sfsdfdsfdsfdsfdsfdsfsdfsdfsdfsdfsdsfsff")
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.layout_image, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        Log.d("TAG", "onBindViewHolder: sdfdsfdsfdsfdsfsdffsdf")
        holder.setIsRecyclable(false)
        holder.overlay.visibility = View.VISIBLE
        holder.mSelectImage.setColorFilter(color)

        holder.overlay.setOnClickListener {
            //Handling
        }
//        if (Constant.lastSelectedPosition == position) {
//            holder.mSelectImage.visibility = View.VISIBLE
//        } else {
//            holder.mSelectImage.visibility = View.INVISIBLE
//        }
        Glide.with(mContext)
            .load(mediaUris[position].uri)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    //TODO: something on exception
                    mediaUris[position].isCorrupted = true
                    holder.mImage.setBackgroundColor(Color.WHITE)
                    holder.mImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.corrupt_file_black
                        )
                    )
                    holder.overlay.visibility = View.GONE
                    holder.mImage.setPadding(50, 50, 50, 50)
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    Log.d("TAG", "OnResourceReady")
                    //do something when picture already loaded
                    mediaUris[position].isCorrupted = false
                    holder.overlay.visibility = View.GONE
                    holder.mImage.setBackgroundColor(0)
                    holder.mImage.setPadding(0, 0, 0, 0)
//                    holder.mImage.setImageDrawable(resource)
                    return false
                }
            })
            .placeholder(R.drawable.place_holder_photo).into(holder.mImage)

        holder.itemView.setOnClickListener {
//            if(!isLongClickVideo) {
                if (mediaUris[position].isCorrupted) {
                    mCorrupt.onCorruptImageClicked()

//                AlertDialog.Builder(mContext) //  .setTitle("Select another image")
//                    .setMessage("This image is corrupted. Please select another image.")
//                    .setPositiveButton("Ok") { dialog, which -> dialog.dismiss() }
//                    .show()
                } else {
                    Constant.lastSelectedPosition = position
                    listener.selectMedia(mediaUris[position])
//                    notifyDataSetChanged()
                    notifyItemChanged(position, holder.mSelectImage)

                }
//            }
//            else{
//
//                if (mediaUris[position].isCorrupted) {
//                    mCorrupt.onCorruptImageClicked()
//
////                AlertDialog.Builder(mContext) //  .setTitle("Select another image")
////                    .setMessage("This image is corrupted. Please select another image.")
////                    .setPositiveButton("Ok") { dialog, which -> dialog.dismiss() }
////                    .show()
//                } else {
//                    Constant.lastSelectedPosition = position
//                    listener.selectMedia(mediaUris[position])
//                    notifyDataSetChanged()
//
//                }
//            }

        }

        if(mediaUris[position].isSelected){
            holder.mSelectImage.visibility = View.VISIBLE
        }
        else{
            holder.mSelectImage.visibility = View.GONE
        }

    }

    fun removeSelection() {
        Constant.lastSelectedPosition = -1
    }

    override fun getItemCount(): Int {
        Log.d("TAG", "getItemCount: adsfsfsfdsfdsfdsf")
        return mediaUris.size
    }
}