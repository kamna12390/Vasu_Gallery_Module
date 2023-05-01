package com.vasu.image.video.pickrandom.galleryapp.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
import com.vasu.image.video.pickrandom.galleryapp.model.Album

class AlbumAdapterVideo (var albumActivity: ImagePickerActivity , val color : Int, var mAlbumList: List<Album>, var listener : OnAlbumSelected) :
    RecyclerView.Adapter<AlbumAdapterVideo.ViewHolder>() {

    interface OnAlbumSelected {
        fun onAlbumClicked(position: Int, folderName: String)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mThumbImage = itemView.findViewById<ImageView>(R.id.ivThumbImage)!!
        var mTxtAlbum = itemView.findViewById<TextView>(R.id.txtAlbumName)!!
        var mTxtAlbumCount = itemView.findViewById<TextView>(R.id.txtCount)!!
        var frameLayout = itemView.findViewById<LinearLayout>(R.id.frameLayout)!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(albumActivity).inflate(R.layout.layout_album, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mTxtAlbum.text = mAlbumList[position].name
        if(Constant.folderTitleColor!=null) {
            holder.mTxtAlbum.setTextColor(Color.parseColor(Constant.folderTitleColor))
        }
        else{
            holder.mTxtAlbum.setTextColor(Color.WHITE)
        }
        holder.mTxtAlbumCount.text = mAlbumList[position].mediaCount.toString()
        if(Constant.folderCountColor!=null) {
            holder.mTxtAlbumCount.setTextColor(Color.parseColor(Constant.folderCountColor))
        }
        else{
            holder.mTxtAlbumCount.setTextColor(Color.WHITE)
        }
        holder.frameLayout.setBackgroundColor(color)


        Glide.with(albumActivity)
            .load(mAlbumList[position].thumbnailUri)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    //TODO: something on exception
                    holder.mThumbImage.setImageDrawable(ContextCompat.getDrawable(albumActivity, R.drawable.corrupt_file_black))
                    holder.mThumbImage.setPadding(50, 50, 50, 50)
//                    mediaUris[position].isCorrupted = true
//                    holder.mImage.setBackgroundColor(Color.WHITE)
//                    holder.mImage.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            mContext,
//                            R.drawable.corrupt_file_black
//                        )
//                    )
//                    holder.overlay.visibility = View.GONE
//                    holder.mImage.setPadding(50, 50, 50, 50)
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
                    holder.mThumbImage.setPadding(0, 0, 0, 0)

//                    holder.mImage.setImageDrawable(resource)
                    return false
                }
            })
            .placeholder(R.drawable.place_holder_photo).into(holder.mThumbImage)


        holder.itemView.setOnClickListener {
            listener.onAlbumClicked(position,mAlbumList[position].name)
        }
    }

    override fun getItemCount(): Int {
        return mAlbumList.size
    }
}