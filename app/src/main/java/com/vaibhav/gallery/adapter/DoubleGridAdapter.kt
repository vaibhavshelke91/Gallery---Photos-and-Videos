package com.vaibhav.gallery.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.vaibhav.gallery.R
import com.vaibhav.gallery.model.ImageModel
import com.vaibhav.gallery.collectiondb.store.tool.DynamicImageView
import com.vaibhav.gallery.collectiondb.store.tool.makeAnimation
import java.io.File


class DoubleGridAdapter(val fragment:Fragment) : SelectableRecycleViewAdapter<DoubleGridAdapter.ViewHolder>() {
    var list=ArrayList<ImageModel>()
    var titleVisibility=true

    inner class ViewHolder(v: View) :RecyclerView.ViewHolder(v),View.OnClickListener,View.OnLongClickListener{

        var imageView: DynamicImageView
        var textView:TextView
        var mView:View
        var checkBox:CheckBox
        val card:MaterialCardView
        init {
            imageView=v.findViewById(R.id.img)
            textView=v.findViewById(R.id.title)
            mView=v.findViewById(R.id.mView)
            card=v.findViewById(R.id.mCard)
            checkBox=v.findViewById(R.id.mCheckBox)
            card.setOnClickListener(this)
            card.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            if (onItemClick != null) {
                onItemClick!!.onItemClick(imageView, list[absoluteAdapterPosition], absoluteAdapterPosition)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            if (onItemClick != null) {
                onItemClick!!.onLongPress(imageView, list[absoluteAdapterPosition], absoluteAdapterPosition)
            }
            return true
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DoubleGridAdapter.ViewHolder {
       return ViewHolder(
           LayoutInflater.from(parent.context)
               .inflate(R.layout.stagg_layout,parent,false)
       )
    }

    override fun onBindViewHolder(holder: DoubleGridAdapter.ViewHolder, position: Int) {
        if (list.isEmpty()){
            return
        }
        var height=list[position].height.toFloat()
        var width=list[position].width.toFloat()
        val orientation=list[position].orientation

        if (height==0f){
            height=1f
        }
        if (width==0f){
            width=1f
        }

        val params=holder.imageView.layoutParams as ConstraintLayout.LayoutParams

            val ratio = height / width
            params.height = (ratio * params.width).toInt()
            holder.imageView.layoutParams = params
            holder.imageView.setRatio(ratio)

       holder.imageView.post {
            if (width>height) {
                if (fragment.context!=null && list.isNotEmpty()) {
                    Glide.with(fragment)
                        .load(File(list[position].path))
                        .centerCrop()
                        .into(holder.imageView)

                }


            }else{
                if (fragment.context!=null && list.isNotEmpty()) {
                    Glide.with(fragment)
                        .load(File(list[position].path))
                        .into(holder.imageView)
                }

            }
        }
        if (sync){

            if (list.isNotEmpty() && !File(list[position].path).exists()){
                if (onErrorListeners!=null){
                    onErrorListeners?.onFileNoFound(list[position],position)
                    Log.d("GridAdapter","File Deleted ${list[position].name}")
                }
            }
        }else{

        }

        holder.textView.text=list[position].name
        if (titleVisibility){
            holder.textView.visibility=View.VISIBLE
        }else{
            holder.textView.visibility=View.GONE
        }

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



    fun isValidContextForGlide(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        if (context is Activity) {
            val activity = context
            if (activity.isDestroyed || activity.isFinishing) {
                return false
            }
        }
        return true
    }

    fun getSelectedImages(): java.util.ArrayList<ImageModel> {
        val integers = selectedItems
        val models = java.util.ArrayList<ImageModel>()
        for (i in integers.indices) {
            models.add(list[integers[i]])
        }
        return models
    }

    override fun getItemCount(): Int {
     return  list.size
    }


    var onItemClick:OnItemClick?=null
    interface OnItemClick {
        fun onItemClick(view: View, model: ImageModel, position: Int)
        fun onLongPress(view: View, model: ImageModel, position: Int)
    }

    interface OnErrorListeners{
        fun onFileNoFound( model: ImageModel, position: Int)
    }

    var onErrorListeners:OnErrorListeners?=null
    var sync=false
    fun setErrorListeners(onErrorListeners: OnErrorListeners,sync:Boolean){
        this.sync=sync
        this.onErrorListeners=onErrorListeners
    }
}