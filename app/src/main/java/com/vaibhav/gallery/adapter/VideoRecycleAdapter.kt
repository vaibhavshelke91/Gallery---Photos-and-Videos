package com.vaibhav.gallery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vaibhav.gallery.R
import com.vaibhav.gallery.model.ImageModel
import com.vaibhav.gallery.model.VideoModel
import com.vaibhav.gallery.collectiondb.store.tool.DynamicImageView
import java.io.File

class VideoRecycleAdapter(val fragment: Fragment):SelectableRecycleViewAdapter<VideoRecycleAdapter.ViewHolder>() {
    var list= listOf<VideoModel>()
   inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val imageView=view.findViewById<DynamicImageView>(R.id.img)
        val title=view.findViewById<TextView>(R.id.title)
       var mView=view.findViewById<View>(R.id.mView)
       var checkBox=view.findViewById<CheckBox>(R.id.mCheckBox)
        init {
            view.setOnClickListener {
                if (::onClickListeners.isInitialized){
                    onClickListeners.onClick(it,absoluteAdapterPosition,list[absoluteAdapterPosition])
                }
            }
            view.setOnLongClickListener {
                if (::onClickListeners.isInitialized){
                    onClickListeners.onLongPress(it,absoluteAdapterPosition,list[absoluteAdapterPosition])
                }
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoRecycleAdapter.ViewHolder {
      return ViewHolder( LayoutInflater.from(parent.context)
          .inflate(R.layout.video_layout, parent, false))
    }

    override fun onBindViewHolder(holder: VideoRecycleAdapter.ViewHolder, position: Int) {
        var height=list[position].height.toFloat()
        var width=list[position].width.toFloat()
        if (height==0f){
            height=1f
        }
        if (width==0f){
            width=1f
        }
        val params=holder.imageView.layoutParams as ConstraintLayout.LayoutParams
        val ratio=height/width
        params.height=(ratio*params.width).toInt()
        holder.imageView.layoutParams=params
        holder.imageView.setRatio(ratio)
        holder.imageView.post {
            if (width>height) {
                if (fragment.context!=null) {
                    Glide.with(fragment)
                        .load(File(list[position].path))
                        .centerCrop()
                        .into(holder.imageView)
                }
            }else{
                if (fragment.context!=null) {
                    Glide.with(fragment)
                        .load(File(list[position].path))
                        .into(holder.imageView)
                }
            }
        }
        holder.title.text=list[position].name
        if (isSelected(position)) {
            holder.mView.visibility = View.VISIBLE
            holder.checkBox.visibility = View.VISIBLE
            holder.checkBox.isChecked = true
        } else {
            holder.mView.visibility = View.GONE
            holder.checkBox.visibility = View.GONE
            holder.checkBox.isChecked = false
        }

    }
    fun getSelectedVideos(): java.util.ArrayList<VideoModel> {
        val integers = selectedItems
        val models = java.util.ArrayList<VideoModel>()
        for (i in integers.indices) {
            models.add(list[integers[i]])
        }
        return models
    }
    interface OnClickListeners{
        fun onClick(view: View,position:Int,model: VideoModel)
        fun onLongPress(view: View, position: Int,model: VideoModel)
    }

    lateinit var onClickListeners: OnClickListeners

    override fun getItemCount(): Int {
       return list.size
    }
}