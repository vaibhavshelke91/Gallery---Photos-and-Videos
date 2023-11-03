package com.vaibhav.gallery.location

import android.content.Context
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

class LocationRecycleAdapter(private val context: Context):RecyclerView.Adapter<LocationRecycleAdapter.ViewHolder>(){


    var keySet=ArrayList<String>()
    var list=HashMap<String,ArrayList<LocationModel>>()

    fun setLocationModel(model: LocationModel){
        if (list.containsKey(model.name)){
            list[model.name]!!.add(model)
        }else{
            val newList=ArrayList<LocationModel>()
            newList.add(model)
            list[model.name] = newList
        }
        keySet.clear()
        keySet.addAll(list.keys)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var imageView:ImageView
        var textView:TextView
        init {
            imageView=itemView.findViewById(R.id.img)
            textView=itemView.findViewById(R.id.locationName)
        }
    }

    override fun getItemCount(): Int {
      return list.size
    }

    override fun onBindViewHolder(holder: LocationRecycleAdapter.ViewHolder, position: Int) {

        Glide.with(holder.imageView.context)
            .load(File(list[keySet[position]]!![0].imageModel.path))
            .override(300,300)
            .into(holder.imageView)
        holder.textView.text=keySet[position]
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocationRecycleAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.location_layout,parent,false)
        )
    }
}