package com.vaibhav.gallery.model


import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class VideoViewModel():ViewModel(),CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val livedata=MutableLiveData<List<VideoModel>>()

    private fun fetch(context: Context,sortOrder:String):List<VideoModel>{
        val list= mutableListOf<VideoModel>()
        val contentResolver=context.contentResolver
        val uri=MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val cursor=contentResolver.query(uri,null,null,null,sortOrder)

        while (cursor?.moveToNext() == true){
            try {
                val model = VideoModel()
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                model.name =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                model.bucketName =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                model.path =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                model.contentId =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                model.size =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                model.height =
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT))
                model.width =
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH))
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id
                )
                model.contentUri = contentUri.toString()
                list.add(model)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        cursor?.let {
            it.close()
        }
       return  list

    }

    public fun getLiveData():LiveData<List<VideoModel>>{
        return livedata
    }


    fun getVideos(context: Context,sortOrder:String=MediaStore.Video.Media.DATE_ADDED+" DESC"){
        launch(Dispatchers.Main) {
            livedata.value = withContext(Dispatchers.IO) {
              fetch(context,sortOrder)
            }!!
        }
    }

}