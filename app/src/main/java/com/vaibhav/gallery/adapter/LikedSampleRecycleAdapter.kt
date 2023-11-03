package com.vaibhav.gallery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vaibhav.gallery.R
import com.vaibhav.gallery.model.ImageModel
import java.io.File

class LikedSampleRecycleAdapter(var list:ArrayList<ImageModel>,val onClickListener:(pos:Int) -> Unit) :RecyclerView.Adapter<LikedSampleRecycleAdapter.ViewHolder>() {



    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        var imageView:ImageView
        init {
            imageView=view.findViewById(R.id.likedImg)
            view.setOnClickListener {
                onClickListener.invoke(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.liked_sample,parent,false))
    }

    override fun getItemCount(): Int {
        if (list.size>10){
            return 10
        }
       return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.imageView.context!=null) {
            Glide.with(holder.imageView.context)
                .load(File(list[position].path))
                .into(holder.imageView)
        }
    }

}