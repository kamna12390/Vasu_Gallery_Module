package com.vasu.image.video.pickrandom.galleryapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vasu.image.video.pickrandom.galleryapp.databinding.CropImageSizeBinding
import com.vasu.image.video.pickrandom.galleryapp.helper.Constant
import com.vasu.image.video.pickrandom.galleryapp.model.CropModel


class CropAdapter(private val context: Context,
                  val textColor : Int,
                  private val mList: ArrayList<CropModel>,
                  private val cellClickListener: CellClickListener):
    RecyclerView.Adapter<CropAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: CropImageSizeBinding) : RecyclerView.ViewHolder(binding.root)

    private var selectedItemPosition: Int = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = CropImageSizeBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val list = mList[position]

        with(holder){
            with(binding){
                imageview.setImageDrawable(list.image)
                textView.text = list.text
                itemView.setOnClickListener {
                    cellClickListener.onCellClickListener(position,list.aspectRatioX,list.aspectRatioY)
                    selectedItemPosition = position
                   notifyDataSetChanged()
                }
                if(selectedItemPosition == position) {
            textView.setTextColor(textColor)
                    imageview.setColorFilter(textColor)
        }
        else {
            if(Constant.folderCountColor!=null){
                textView.setTextColor(Color.parseColor(Constant.folderCountColor))
                imageview.setColorFilter(Color.parseColor(Constant.folderCountColor))
            }
                    else{
                textView.setTextColor(Color.parseColor("#FFFFFF"))
                imageview.setColorFilter(Color.parseColor("#FFFFFF"))
            }


        }
            }
        }
        Log.d("TAG", "onBindViewHolder: check the position:$position")

    }

    override fun getItemCount(): Int {
        return mList.size
    }


    interface CellClickListener {
        fun onCellClickListener(position: Int, aspectRatioX: Int, aspectRatioY: Int)
    }


}


