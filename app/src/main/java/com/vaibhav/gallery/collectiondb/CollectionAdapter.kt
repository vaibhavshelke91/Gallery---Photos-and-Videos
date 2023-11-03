package com.vaibhav.gallery.collectiondb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vaibhav.gallery.R
import com.vaibhav.gallery.model.ImageModel
import java.io.File

class CollectionAdapter(): RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    var map= HashMap<String,ArrayList<ImageModel>>()
    var list=ArrayList<String>()

    fun update(){
        list.clear()
        list.addAll(map.keys)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val last=view.findViewById<ImageView>(R.id.img3)
        val mid=view.findViewById<ImageView>(R.id.img2)
        val first=view.findViewById<ImageView>(R.id.img1)
        val text=view.findViewById<TextView>(R.id.text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.collection_layout,parent,false)
        )
    }

    override fun getItemCount(): Int {
      return map.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text=list[position]
        val items=map[list[position]]
        items?.let {
            if (items.size>2){
                load(holder.last,items[2].path)
                load(holder.mid,items[1].path)
                load(holder.first,items[0].path)
            }else if (items.size >1){
                load(holder.mid,items[1].path)
                load(holder.first,items[0].path)
            }else if(items.size>0){
                load(holder.first,items[0].path)
            }
        }
    }
    fun load(img:ImageView,path: String){
        Glide.with(img.context)
            .load(File(path))
            .into(img)
    }
}