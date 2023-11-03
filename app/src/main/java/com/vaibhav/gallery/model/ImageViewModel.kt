package com.vaibhav.gallery.model

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class ImageViewModel : ViewModel(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private var imageLiveData: MutableLiveData<kotlin.collections.ArrayList<ImageModel>> = MutableLiveData()

    fun getAudioList(): MutableLiveData<kotlin.collections.ArrayList<ImageModel>> {
        return imageLiveData
    }

    private fun loadImage(context: Context, sortOrder: String):ArrayList<ImageModel>{

        val imageList: ArrayList<ImageModel> = ArrayList()
        var prev=""
        var breakNext=false

            val contentResolver: ContentResolver = context.contentResolver

            val uri =Constant.uri

            val cursor: Cursor? = contentResolver.query(uri, null, null, null, sortOrder)

            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    try {


                        val path: String =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                        val name: String =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                        val bucketName: String =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))

                        val height =
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT))

                        val width =
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH))

                        val size =
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))

                        val date =
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED))

                        val orientation=
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION))

                        val contentId =
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))

                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentId
                        )

                        val sDate = getCurrentDate(date * 1000)

                        /*  if (prev != sDate){
                        prev=sDate
                        breakNext=true

                    }else{
                        breakNext=false
                    }*/

                        val model = ImageModel()
                        model.path = path
                        model.name = name
                        model.bucketName = bucketName
                        model.height = height
                        model.width = width
                        model.size = size
                        model.orientation=orientation
                        model.date = date
                        model.textDate = sDate
                        model.isDuplicate = false
                        model.isBreakable = breakNext
                        model.contentId = contentId
                        model.contentUri = contentUri.toString()
                        imageList.add(model)
                        /*  if (breakNext){
                        val model=ImageModel()
                        model.path=path
                        model.name=name
                        model.bucketName=bucketName
                        model.height=height
                        model.width=width
                        model.size=size
                        model.date=date
                        model.textDate=sDate
                        model.contentId=contentId
                        model.contentUri=contentUri.toString()
                        model.isBreakable=false
                        model.isDuplicate=true
                        imageList.add(model)
                    }*/


                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
          cursor?.let {
              it.close()
          }

        return imageList

    }

   private fun getCurrentDate(timestamp: Long) :String{
        val simpleDateFormat=SimpleDateFormat("dd MMMM yyyy")
        val date=Date(timestamp)
        return simpleDateFormat.format(date).toString()
    }
    private fun getTime(timestamp: Long):String{
        val simpleDateFormat=SimpleDateFormat("hh:mm:ss")
        val date=Date(timestamp)
        return simpleDateFormat.format(date).toString()
    }

    fun getAllImages(context: Context,sortOrder:String=MediaStore.Images.Media.DATE_ADDED+" DESC") {
        launch(Dispatchers.Main) {
            imageLiveData.value = withContext(Dispatchers.IO) {
                loadImage(context,sortOrder)
            }!!
        }
    }

}