package com.vaibhav.gallery.model

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.text.format.DateFormat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.coroutines.CoroutineContext

class FilteredImageModel :ViewModel(),CoroutineScope{

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private var imageLiveData: MutableLiveData<ArrayList<ImageModel>> = MutableLiveData()

    fun getImageList(): MutableLiveData<ArrayList<ImageModel>> {
        return imageLiveData
    }

    private fun load(context:Context,order:String):ArrayList<ImageModel>{
        val list=ArrayList<ImageModel>()
        val contentResolver: ContentResolver = context.contentResolver

        val uri =Constant.uri

        val cursor: Cursor? = contentResolver.query(uri, null, null, null, "RANDOM()")

        if (cursor!=null && cursor.count>0){
            while (cursor.moveToNext()){
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
                    val orientation =
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION))
                    val date =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED))
                    val contentId =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentId
                    )

                    val sDate = getImgDate(date * 1000)
                    if (DateFormat.format("EEEE", Date(date * 1000)) == DateFormat.format(
                            "EEEE",
                            Date()
                        )
                    ) {
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
                        model.isBreakable = false
                        model.contentId = contentId
                        model.contentUri = contentUri.toString()
                        list.add(model)
                        if (list.size == 15) {
                            break
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }
        }

        return list
    }

    private fun getImgDate(timestamp: Long) :String{
        val simpleDateFormat= SimpleDateFormat("dd MMMM yyyy")
        val date= Date(timestamp)
        return simpleDateFormat.format(date).toString()
    }
    private fun getCurrentDate():String{
        val simpleDateFormat= SimpleDateFormat("dd MMMM yyyy")
        val date= Date()
        return simpleDateFormat.format(date).toString()
    }

    fun getAllImages(context: Context,sortOrder:String= MediaStore.Images.Media.DATE_ADDED+" DESC") {
        launch(Dispatchers.Main) {
            imageLiveData.value = withContext(Dispatchers.IO) {
                load(context,sortOrder)
            }!!
        }
    }
}