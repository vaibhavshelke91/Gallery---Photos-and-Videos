package com.vaibhav.gallery.editor


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vaibhav.gallery.R
import com.vaibhav.gallery.databinding.ActivityGpuimageBinding


import java.io.File

class GPUImageActivity : AppCompatActivity() {
    lateinit var binding:ActivityGpuimageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityGpuimageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uri=intent.getStringExtra("p")



        val adapter=FilterRecycleAdapter(this)
        binding.fliterRecycle.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.fliterRecycle.adapter=adapter


    }

}