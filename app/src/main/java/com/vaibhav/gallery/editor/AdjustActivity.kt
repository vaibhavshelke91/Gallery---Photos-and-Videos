package com.vaibhav.gallery.editor

import android.graphics.Bitmap
import android.media.effect.EffectFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.slider.Slider
import com.vaibhav.gallery.databinding.ActivityAdjustBinding

import ja.burhanrashid52.photoeditor.CustomEffect
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.SaveSettings
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class AdjustActivity : AppCompatActivity() {
    lateinit var binding:ActivityAdjustBinding

    lateinit var gpuImage: GPUImage

     private var gpuImageBrightnessFilter= GPUImageBrightnessFilter()
     private var gpuImageContrastFilter= GPUImageContrastFilter()
     private var gpuImageSaturationFilter= GPUImageSaturationFilter()
     private var gpuImageSharpenFilter= GPUImageSharpenFilter()

    var filterList=ArrayList<GPUImageFilter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdjustBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gpuImage=GPUImage(this)
        gpuImage.setGLSurfaceView(binding.imgView)
        gpuImage.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)
        gpuImage.setImage(Static.bitmap)
       // gpuImage.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)


        filterList.add(gpuImageBrightnessFilter)
        filterList.add(gpuImageContrastFilter)
        filterList.add(gpuImageSaturationFilter)
        filterList.add(gpuImageSharpenFilter)

        binding.close.setOnClickListener {
            finish()
        }
        binding.sharperSlider.addOnChangeListener { slider, value, fromUser ->
            setFilters()
        }
        binding.brightnessSlider.addOnChangeListener { slider, value, fromUser ->
            setFilters()
        }
        binding.saturationSlider.addOnChangeListener { slider, value, fromUser ->
           setFilters()
        }
        binding.contrastSlider.addOnChangeListener { slider, value, fromUser ->
            setFilters()
        }

        binding.save.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                Static.bitmap=gpuImage.bitmapWithFilterApplied
                CoroutineScope(Dispatchers.Main).launch {
                    finish()
                }
            }
        }

    }

    private fun setFilters(){
        gpuImageBrightnessFilter.setBrightness(binding.brightnessSlider.value)
        gpuImageContrastFilter.setContrast(binding.contrastSlider.value)
        gpuImageSaturationFilter.setSaturation(binding.saturationSlider.value)
        gpuImageSharpenFilter.setSharpness(binding.sharperSlider.value)

        val filterGroup=GPUImageFilterGroup(filterList)
        gpuImage.setFilter(filterGroup)
    }



}