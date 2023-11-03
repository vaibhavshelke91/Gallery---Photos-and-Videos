package com.vaibhav.gallery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vaibhav.gallery.R
import com.vaibhav.gallery.model.ImageModel
import com.vaibhav.gallery.model.VideoModel
import com.vaibhav.gallery.collectiondb.store.tool.DynamicImageView
import java.io.File

class VideoSearchAdapter (private val context: Context):RecyclerView.Adapter<VideoSearchAdapter.ViewHolder>() {

    inner class ViewHolder(view:View) : RecyclerView.ViewHolder(view) {
        lateinit var img: ImageView
        lateinit var name:TextView
        lateinit var bucket:TextView
        init {
            img=view.findViewById(R.id.image)
            name=view.findViewById(R.id.name)
            bucket=view.findViewById(R.id.bucket)
            view.setOnClickListener {
                if (::onClickListeners.isInitialized){
                    onClickListeners.onClick(it,absoluteAdapterPosition,list[absoluteAdapterPosition])
                }
            }
        }
    }
    var list= mutableListOf<VideoModel>()
    fun setList(list: ArrayList<VideoModel>){
        this.list=list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoSearchAdapter.ViewHolder {
        return ViewHolder( LayoutInflater.from(parent.context)
            .inflate(R.layout.search_layout
                ,parent
                ,false)
        )
    }

    override fun onBindViewHolder(holder: VideoSearchAdapter.ViewHolder, position: Int) {
        if (holder.img.context!=null) {
            Glide.with(holder.img.context)
                .load(File(list[position].path))
                .apply(RequestOptions().override(200, 200))
                .into(holder.img)
        }
        holder.name.text=list[position].name
        holder.bucket.text=list[position].bucketName
    }

    override fun getItemCount(): Int {
        return list.size

    }
    lateinit var onClickListeners:OnClickListeners

    interface OnClickListeners{
        fun onClick(view: View,position: Int,model: VideoModel)
    }
}