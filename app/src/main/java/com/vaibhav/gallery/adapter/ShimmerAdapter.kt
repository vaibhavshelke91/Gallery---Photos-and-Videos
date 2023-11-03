package com.vaibhav.gallery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.vaibhav.gallery.databinding.ShimmerLayoutBinding

class ShimmerAdapter(val context: Context,val list: List<StarterTemp>) :RecyclerView.Adapter<ShimmerAdapter.ViewHolder>(){
    inner class ViewHolder(val binding: ShimmerLayoutBinding) :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShimmerAdapter.ViewHolder {
       val layout=ShimmerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(layout);
    }

    override fun onBindViewHolder(holder: ShimmerAdapter.ViewHolder, position: Int) {
        var height=list[position].height.toFloat()
        var width=list[position].width.toFloat()
        if (height==0f){
            height=1f
        }
        if (width==0f){
            width=1f
        }
        val params=holder.binding.img.layoutParams as FrameLayout.LayoutParams
        val ratio=height/width
        params.height=(ratio*params.width).toInt()
        holder.binding.img.layoutParams=params
        holder.binding.img.setRatio(ratio)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    data class StarterTemp(var height:Int,
    var width:Int)

}