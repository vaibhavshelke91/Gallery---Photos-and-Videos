package com.vaibhav.gallery.location

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.vaibhav.gallery.model.ImageModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.CoroutineContext


class LocationViewModel : ViewModel(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private var imageLiveData: MutableLiveData<LocationModel> = MutableLiveData()

    fun getAlbumList(): MutableLiveData<LocationModel> {
        return imageLiveData
    }

    private fun  getAddress(context: Context, lat: Double, lng: Double) :Address?{
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            return addresses!![0]

        } catch (e: IOException) {

            e.printStackTrace()

        }
        return null
    }

    private fun loadImage(context: Context, sortOrder: String){

        //val imageList= HashMap<String,LocationModel>()

        try {

            val contentResolver: ContentResolver = context.contentResolver


            val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

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


                    val contentUri= ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        ,contentId)

                    val sDate=getCurrentDate(date*1000)



                    try {
                        var uri=contentUri
                        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                            uri=MediaStore.setRequireOriginal(uri)
                        }
                        val stream=context.contentResolver.openInputStream(uri)

                        val exifInterface = ExifInterface(stream!!)
                        val latLong = exifInterface.latLong
                        if (latLong!= null){
                            val addresses=getAddress(context,latLong[0],latLong[1])
                            if (addresses!=null){
                                val locationModel=LocationModel()
                                locationModel.lat=latLong[0].toLong()
                                locationModel.lon=latLong[1].toLong()
                                locationModel.address=addresses
                                locationModel.name=addresses.locality
                                val model= ImageModel()
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
                                locationModel.imageModel=model
                                CoroutineScope(Dispatchers.Main).launch {
                                    imageLiveData.value=locationModel
                                }
                                stream.close()
                            }
                        }
                    } catch (e: IOException) {
                       e.printStackTrace()
                    }

                }
            }
            assert(cursor != null)
            cursor!!.close()
        }catch (e:Exception){
            e.printStackTrace()
        }


    }

    private fun getCurrentDate(timestamp: Long) :String{
        val simpleDateFormat= SimpleDateFormat("dd MMMM yyyy")
        val date= Date(timestamp)
        return simpleDateFormat.format(date).toString()
    }
    private fun getTime(timestamp: Long):String{
        val simpleDateFormat= SimpleDateFormat("hh:mm:ss")
        val date= Date(timestamp)
        return simpleDateFormat.format(date).toString()
    }

    fun getAllImages(context: Context, progressBar: LinearProgressIndicator,sortOrder:String= MediaStore.Audio.Media.DATE_ADDED+" DESC") {
        launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                loadImage(context,sortOrder)
                CoroutineScope(Dispatchers.Main).launch {
                    progressBar.visibility=View.GONE
                }
            }!!
        }
    }

}