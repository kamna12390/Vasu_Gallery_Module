package com.vasu.image.video.pickrandom.galleryapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.vasu.image.video.pickrandom.galleryapp.databinding.ShapeCropBinding
import com.vasu.image.video.pickrandom.galleryapp.model.CropModel

class ShapeAdapter(private val context: Context,
                   val textColor : Int,
                   val bgColor : Int,
                   val unselectedColor : Int,
                   private val mList: List<CropModel>, private val cellClickListener: CellClickListener): RecyclerView.Adapter<ShapeAdapter.ViewHolder>() {


    private var selectedItemPosition: Int = 0
    inner class ViewHolder(val binding: ShapeCropBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ShapeCropBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val ItemsViewModel = mList[position]
        with(holder){
            with(binding){
                imageview.setImageDrawable(ItemsViewModel.image)
                textView.text = ItemsViewModel.text
                itemView.setOnClickListener {
                    cellClickListener.onCellClickListener(position)
                    selectedItemPosition = position
                    notifyDataSetChanged()
                }
                if(selectedItemPosition == position) {
                    textView.setTextColor(textColor)
                    imageview.setColorFilter(bgColor)
                }
                else {
                    textView.setTextColor(unselectedColor)
                    imageview.setColorFilter(unselectedColor)

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    interface CellClickListener {
        fun onCellClickListener(position: Int)
    }
}