package com.vaibhav.gallery


import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build

import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.addCallback

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.dialog.MaterialAlertDialogBuilder


import com.vaibhav.gallery.databinding.ActivityEditorBinding
import com.vaibhav.gallery.editor.AdjustActivity
import com.vaibhav.gallery.editor.CropperAndRotateActivity
import com.vaibhav.gallery.editor.DrawActivity
import com.vaibhav.gallery.editor.Static

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class EditorActivity : AppCompatActivity() {
    lateinit var binding:ActivityEditorBinding

   private suspend fun copy(path:String){
       withContext(Dispatchers.IO){
           val file=File(path)
           Static.bitmap=BitmapFactory.decodeFile(file.absolutePath)
           Log.d("File","Created ")

       }
   }

    override fun onStart() {
        if (Static.bitmap!=null){
            lifecycleScope.launch{
                Glide.with(this@EditorActivity)
                    .load(Static.bitmap)
                    .into(binding.mPhotoEditor)
            }
            Log.d("Cropped","True")
        }else{
            Log.d("Cropped","False")
        }

        super.onStart()

    }

    override fun onDestroy() {
        Static.bitmap=null
        super.onDestroy()

    }

    private suspend fun load(file: File):Bitmap{
        return withContext(Dispatchers.IO){
            BitmapFactory.decodeFile(file.absolutePath)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val path = intent.getStringExtra("p")

       lifecycleScope.launch {
           copy(path!!)
           binding.topHeader.visibility=View.VISIBLE
           binding.bottomHeader.visibility=View.VISIBLE
       }

        Glide.with(this)
            .load(File(path))
            .into(binding.mPhotoEditor)



        binding.back.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("${resources.getString(R.string.save)}")
                .setMessage("${resources.getString(R.string.save_without)}")
                .setPositiveButton("${resources.getString(R.string.yes)}") { it, id ->
                    it.dismiss()
                    finish()
                }
                .setNegativeButton("${resources.getString(R.string.no)}") { it, id ->

                }.create().show()
        }

        binding.draw.setOnClickListener {
            draw()
        }


        binding.crop.setOnClickListener {
            crop()
        }
        binding.adjust.setOnClickListener {
            adjust()
        }
        binding.save.setOnClickListener {
            lifecycleScope.launch {
                saveMediaToStorage(Static.bitmap)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this){
            MaterialAlertDialogBuilder(this@EditorActivity)
                .setTitle("${resources.getString(R.string.save)}")
                .setMessage("${resources.getString(R.string.save_without)}")
                .setPositiveButton("${resources.getString(R.string.yes)}") { it, id ->
                    it.dismiss()
                    finish()
                }
                .setNegativeButton("${resources.getString(R.string.no)}") { it, id ->

                }.create().show()
        }
    }


    private fun adjust(){
        startActivity(Intent(this,AdjustActivity::class.java))
    }

    private fun draw(){
        startActivity(Intent(this,DrawActivity::class.java))
    }

   private fun sticker(){
   }

    private fun crop(){
        startActivity(Intent(this,CropperAndRotateActivity::class.java))
    }

    suspend fun saveMediaToStorage(bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            //Generating a file name
            val filename = "image_${System.currentTimeMillis()}.jpg"

            //Output stream
            var fos: OutputStream? = null

            //For devices running android >= Q
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //getting the contentResolver
                contentResolver?.also { resolver ->

                    //Content resolver will process the contentvalues
                    val contentValues = ContentValues().apply {

                        //putting file information in content values
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }

                    //Inserting the contentValues to contentResolver and getting the Uri
                    val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                    //Opening an outputstream with the Uri that we got
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            } else {
                //These for devices running on android < Q
                //So I don't think an explanation is needed here
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }

            fos?.use {
                //Finally writing the bitmap to the output stream that we opened
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
        }
    }
}

