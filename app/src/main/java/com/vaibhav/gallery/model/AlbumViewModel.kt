package com.vaibhav.gallery.model

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext

class AlbumViewModel : ViewModel(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private var imageLiveData: MutableLiveData<ArrayList<AlbumModel>> = MutableLiveData()

    fun getAlbumList(): MutableLiveData<kotlin.collections.ArrayList<AlbumModel>> {
        return imageLiveData
    }

    private fun loadImage(context: Context, sortOrder: String):kotlin.collections.ArrayList<AlbumModel>{

        val imageList= kotlin.collections.ArrayList<AlbumModel>()

        try {
            var pos=0
            var isFolder=false
            val contentResolver: ContentResolver = context.contentResolver

            val uri =Constant.uri

            val cursor: Cursor? = contentResolver.query(uri, null, null, null, sortOrder)

            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val path: String =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                    val name: String =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                    val bucketName: String =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))

                    val height=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT))

                    val width=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH))

                    val size=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))

                    val date=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED))

                    val bId=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID))

                    val contentId=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))

                    val contentUri= ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        ,contentId)

                    val sDate=getCurrentDate(date*1000)

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
                    model.isDuplicate=false

                    for (i in imageList.indices){
                        if (imageList[i].id==bId){
                            isFolder=true
                            pos=i
                            break
                        }else{
                            isFolder=false
                        }
                    }
                    if (isFolder){
                        var imgList=java.util.ArrayList<ImageModel>()
                        imgList.addAll(imageList[pos].models)
                        imgList.add(model)
                        imageList[pos].models=imgList
                    }else{
                        var imgList=java.util.ArrayList<ImageModel>()
                        imgList.add(model)
                        val albumModel=AlbumModel()
                        albumModel.path=path
                        albumModel.id=bId
                        albumModel.name=bucketName
                        albumModel.models=imgList
                        imageList.add(albumModel)
                    }


                }
            }
           cursor?.let {
               it.close()
           }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return imageList

    }

    private fun getCurrentDate(timestamp: Long) :String{
        val simpleDateFormat= SimpleDateFormat("dd MMMM yyyy")
        val date= Date(timestamp)
        return simpleDateFormat.format(date).toString()
    }
    private fun getTime(timestamp: Long):String{
        val simpleDateFormat=SimpleDateFormat("hh:mm:ss")
        val date=Date(timestamp)
        return simpleDateFormat.format(date).toString()
    }

    fun getAllImages(context: Context, sortOrder:String= MediaStore.Audio.Media.DATE_ADDED+" DESC") {
        launch(Dispatchers.Main) {
            imageLiveData.value = withContext(Dispatchers.IO) {
                loadImage(context,sortOrder)
            }!!
        }
    }

}