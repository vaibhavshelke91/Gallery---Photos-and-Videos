package com.vaibhav.gallery.model

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Im
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class AlbumSelectionModel : ViewModel(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private var imageLiveData: MutableLiveData<ArrayList<ImageModel>> = MutableLiveData()

    fun getAlbumList(): MutableLiveData<ArrayList<ImageModel>> {
        return imageLiveData
    }

    private fun loadImage(context: Context, id:Int,sortOrder: String):kotlin.collections.ArrayList<ImageModel>{

        val imageList= kotlin.collections.ArrayList<ImageModel>()

        try {

            val contentResolver: ContentResolver = context.contentResolver

            val uri =Constant.uri
            val selection=MediaStore.Images.Media.BUCKET_ID+" = ?"
            val args= arrayOf("$id")

            val cursor: Cursor? = contentResolver.query(uri, null, selection, args, sortOrder)

            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
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

                    val bId =
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID))

                    val contentId=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))

                    val contentUri= ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        ,contentId)
                    val orientation=
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION))

                    val sDate = getCurrentDate(date * 1000)


                    val model = ImageModel()
                    model.path = path
                    model.name = name
                    model.bucketName = bucketName
                    model.height = height
                    model.width = width
                    model.size = size
                    model.date = date
                    model.orientation=orientation
                    model.textDate = sDate
                    model.isDuplicate=false
                    model.contentId=contentId
                    model.contentUri=contentUri.toString()

                    imageList.add(model)

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

    fun getAllImages(context: Context, id:Int,sortOrder:String= MediaStore.Audio.Media.DATE_ADDED+" DESC") {
        launch(Dispatchers.Main) {
            imageLiveData.value = withContext(Dispatchers.IO) {
                loadImage(context,id,sortOrder)
            }!!
        }
    }

}