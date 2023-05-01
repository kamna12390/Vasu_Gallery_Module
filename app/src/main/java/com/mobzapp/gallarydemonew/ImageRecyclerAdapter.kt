package com.mobzapp.gallarydemonew

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.mobzapp.gallarydemonew.databinding.ImageItemBinding
import com.vasu.image.video.pickrandom.galleryapp.R
import java.io.IOException

class ImageRecyclerAdapter(var context: Context, var listOfUris : ArrayList<Uri>) : RecyclerView.Adapter<ImageRecyclerAdapter.ViewHolder>()  {
    class ViewHolder(val bind: ImageItemBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bind = ImageItemBinding.inflate(LayoutInflater.from(context))
        return ViewHolder(bind)
    }

    override fun getItemCount(): Int {
        return listOfUris.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("TAG", "onBindViewHolder: dsfdsfdsfdsfdsfdsfdsfdsfds")
        Glide.with(context).asBitmap().load(listOfUris[position]).diskCacheStrategy(
            DiskCacheStrategy.ALL).priority(Priority.IMMEDIATE).skipMemoryCache(true).addListener(object :
            RequestListener<Bitmap?> {
            override fun onLoadFailed(
                e: GlideException?,
                model1: Any?,
                target: com.bumptech.glide.request.target.Target<Bitmap?>?,
                isFirstResource: Boolean
            ): Boolean {
                holder.bind.imageItem.setBackgroundColor(Color.WHITE)
                holder.bind.imageItem.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.corrupt_file_black
                    )
                )
                return true
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model1: Any,
                target: com.bumptech.glide.request.target.Target<Bitmap?>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {

                holder.bind.imageItem.setImageBitmap(resource)
                return true
            }
        }).placeholder(R.drawable.place_holder_photo).into(holder.bind.imageItem)

    }

    fun updateList(list : ArrayList<Uri>){
        listOfUris = list
        notifyDataSetChanged()
    }
}