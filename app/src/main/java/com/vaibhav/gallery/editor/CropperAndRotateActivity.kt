package com.vaibhav.gallery.editor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.callback.LoadCallback
import com.vaibhav.gallery.R
import com.vaibhav.gallery.databinding.ActivityCropperAndRotateBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class CropperAndRotateActivity : AppCompatActivity() {
    lateinit var binding:ActivityCropperAndRotateBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding= ActivityCropperAndRotateBinding.inflate(layoutInflater)
        setContentView(binding.root)


       // val file=File(filesDir,"edit.jpg")



        lifecycleScope.launch{
            Glide.with(this@CropperAndRotateActivity)
                .load(Static.bitmap)
                .into(binding.cropImageView)
        }

        binding.cropImageView.setCropMode(CropImageView.CropMode.FREE)

        binding.rotate.setOnClickListener {
            binding.cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D,500)
        }

        binding.free.setOnClickListener {
            binding.cropImageView.setCropMode(CropImageView.CropMode.FREE)
        }
        binding.r169.setOnClickListener {
            binding.cropImageView.setCropMode(CropImageView.CropMode.RATIO_16_9)
        }
        binding.r34.setOnClickListener {
            binding.cropImageView.setCropMode(CropImageView.CropMode.RATIO_3_4)
        }
        binding.r43.setOnClickListener {
            binding.cropImageView.setCropMode(CropImageView.CropMode.RATIO_4_3)
        }
        binding.fit.setOnClickListener {
            binding.cropImageView.setCropMode(CropImageView.CropMode.FIT_IMAGE)
        }
        binding.square.setOnClickListener {
            binding.cropImageView.setCropMode(CropImageView.CropMode.SQUARE)
        }

        binding.close.setOnClickListener {
            finish()
        }
        binding.save.setOnClickListener {

        CoroutineScope(Dispatchers.IO).launch {
            binding.cropImageView.setCompressFormat(Bitmap.CompressFormat.JPEG)
            binding.cropImageView.setCompressQuality(100)
            Static.bitmap=binding.cropImageView.croppedBitmap
            CoroutineScope(Dispatchers.Main).launch {
                finish()
            }
        }

        }

    }
    private suspend fun load(file: File):Bitmap{
       return withContext(Dispatchers.IO){
            BitmapFactory.decodeFile(file.absolutePath)
        }
    }
}