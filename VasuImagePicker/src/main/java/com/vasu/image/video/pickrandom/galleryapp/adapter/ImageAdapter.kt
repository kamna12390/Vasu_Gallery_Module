package com.vasu.image.video.pickrandom.galleryapp.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
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

class ImageAdapter(
    var mContext: ImagePickerActivity,
    var color: Int,
    var mediaUris: List<Media>,
    var listener: OnImageClick,
    var mCorrupt: OnCorruptClick,
) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    private var lastPosition = -1


    interface OnImageClick {
        fun selectMedia(uri: Media, pos: Int)
    }

    interface OnCorruptClick {
        fun onCorruptImageClicked()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mImage = itemView.findViewById<ImageView>(R.id.ivImage)!!
        var mSelectImage = itemView.findViewById<ImageView>(R.id.ivSelectImage)!!
        var overlay = itemView.findViewById<View>(R.id.overlay)!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.layout_image, parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
//        holder.setIsRecyclable(false)
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
        Log.d("TAG", "onBindViewHolder currpted images: ${mediaUris[position].uri} $position")
//        Glide.with(mContext).asBitmap().load(mediaUris[position].uri).into(holder.mImage)

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


//         Glide.with(mContext).asBitmap().load(mediaUris[position].uri).diskCacheStrategy(
//            DiskCacheStrategy.ALL).priority(Priority.IMMEDIATE).override(300 , 300).skipMemoryCache(true).addListener(object :
//            RequestListener<Bitmap?> {
//            override fun onLoadFailed(
//                e: GlideException?,
//                model1: Any,
//                target: Target<Bitmap?>,
//                isFirstResource: Boolean,
//            ): Boolean {
//                mediaUris[position].isCorrupted = true
//                holder.mImage.setBackgroundColor(Color.WHITE)
//                holder.mImage.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        mContext,
//                        R.drawable.corrupt_file_black
//                    )
//                )
//                holder.overlay.visibility = View.GONE
//                holder.mImage.setPadding(50, 50, 50, 50)
//                return true
//            }
//
//            override fun onResourceReady(
//                resource: Bitmap?,
//                model1: Any,
//                target: Target<Bitmap?>,
//                dataSource: DataSource,
//                isFirstResource: Boolean,
//            ): Boolean {
//                mediaUris[position].isCorrupted = false
////                holder.overlay.visibility = View.GONE
//                holder.mImage.setBackgroundColor(0)
////                holder.mImage.setPadding(0, 0, 0, 0)
//                holder.mImage.setImageBitmap(resource)
//                return true
//            }
//        }).placeholder(R.drawable.place_holder_photo).into(holder.mImage)

        holder.itemView.setOnClickListener {
//            if(!isLongClick) {
            if (mediaUris[position].isCorrupted) {
                mCorrupt.onCorruptImageClicked()

//                AlertDialog.Builder(mContext) //  .setTitle("Select another image")
//                    .setMessage("This image is corrupted. Please select another image.")
//                    .setPositiveButton("Ok") { dialog, which -> dialog.dismiss() }
//                    .show()
            } else {
                Constant.lastSelectedPosition = position
                listener.selectMedia(mediaUris[position], position)
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
//                    listener.selectMedia(mediaUris[position] , position)
//                    notifyDataSetChanged()
//
//                }
//            }

        }


//        holder.itemView.setOnLongClickListener {
//            if (mediaUris[position].isCorrupted) {
//                mCorrupt.onCorruptImageClicked()
//
////                AlertDialog.Builder(mContext) //  .setTitle("Select another image")
////                    .setMessage("This image is corrupted. Please select another image.")
////                    .setPositiveButton("Ok") { dialog, which -> dialog.dismiss() }
////                    .show()
//            } else {
//                Constant.lastSelectedPosition = position
//                onLong.onLongClick(mediaUris[position])
//
//                notifyDataSetChanged()
//
//            }
//            return@setOnLongClickListener true
//        }

        if (mediaUris[position].isSelected) {
            holder.mSelectImage.visibility = View.VISIBLE
        } else {
            holder.mSelectImage.visibility = View.GONE
        }

    }

    fun removeSelection() {
        Constant.lastSelectedPosition = -1
    }

    override fun getItemCount(): Int {
        return mediaUris.size
    }
}