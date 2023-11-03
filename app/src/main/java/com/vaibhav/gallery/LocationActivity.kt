package com.vaibhav.gallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.vaibhav.gallery.databinding.ActivityLocationBinding
import com.vaibhav.gallery.location.LocationRecycleAdapter
import com.vaibhav.gallery.location.LocationViewModel

class LocationActivity : AppCompatActivity() {
    lateinit var binding: ActivityLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mRecycle.layoutManager=GridLayoutManager(this,2)
        val adapter=LocationRecycleAdapter(this)
        binding.mRecycle.adapter=adapter
        val model=ViewModelProvider(this)[LocationViewModel::class.java]
        model.getAlbumList().observe(this){
            adapter.setLocationModel(it)
        }
        model.getAllImages(this,binding.mProgress)
    }
}