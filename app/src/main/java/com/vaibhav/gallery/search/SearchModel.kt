package com.vaibhav.gallery.search

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vaibhav.gallery.model.AlbumModel
import com.vaibhav.gallery.model.Constant
import com.vaibhav.gallery.model.ImageModel
import com.vaibhav.gallery.model.VideoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date


class SearchModel :ViewModel() {

    private val liveData=MutableLiveData<ArrayList<ImageModel>>()

    private val albumData=MutableLiveData<ArrayList<AlbumModel>>()

    private val videoData=MutableLiveData<ArrayList<VideoModel>>()

    fun getVideoData():LiveData<ArrayList<VideoModel>>{
        return videoData
    }

    fun setVideoQuery(context: Context,query:String){
        CoroutineScope(Dispatchers.Main).launch {
            videoData.value=getByVideo(context,query)
        }
    }

    public fun getLiveData():LiveData<ArrayList<ImageModel>>{
        return liveData
    }

    public fun query(context: Context,string: String){
        CoroutineScope(Dispatchers.Main).launch{
            liveData.value=setQuery(context,string)
        }
    }

    public fun getAlbumData():LiveData<ArrayList<AlbumModel>>{
        return albumData
    }

    public fun setAlbumQuery(context: Context,string: String){
        CoroutineScope(Dispatchers.Main).launch {
            albumData.value=getByAlbum(context,string)
        }
    }

    private suspend fun getByVideo(context: Context,string: String):ArrayList<VideoModel>{
        return withContext(Dispatchers.IO){
            val list= ArrayList<VideoModel>()
            val contentResolver=context.contentResolver
            val uri=MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val selection=MediaStore.Video.Media.DISPLAY_NAME+" like ?"
            val args= arrayOf("%$string%")
            val sortOrder=MediaStore.Video.Media.DATE_ADDED+" DESC"

            val cursor=contentResolver.query(uri,null,selection,args,sortOrder)
            if (cursor!=null && cursor.count>0){
                try {
                    while (cursor.moveToNext()){
                        val model=VideoModel()
                        val id=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                        model.name=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                        model.bucketName=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                        model.path=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                        model.contentId=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                        model.size=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                        model.height=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT))
                        model.width=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH))
                        val contentUri= ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            ,id)
                        model.contentUri=contentUri.toString()
                        list.add(model)
                    }
                }catch (ex:Exception){
                    ex.printStackTrace()
                }

            }
            cursor?.let {
                cursor.close()
            }
            list
        }
    }

    private suspend fun getByAlbum(context: Context,string: String):ArrayList<AlbumModel>{
        return withContext(Dispatchers.IO){
            val imageList=ArrayList<AlbumModel>()
            val contentResolver: ContentResolver = context.contentResolver

            val uri =Constant.uri
            val selection=MediaStore.Images.Media.BUCKET_DISPLAY_NAME+" like ?"
            val args= arrayOf("%$string%")
            val sortOrder=MediaStore.Audio.Media.DATE_ADDED+" DESC"

            val cursor: Cursor? = contentResolver.query(uri, null, selection, args, sortOrder)
            var pos=0
            var isFolder=false

            if (cursor!=null && cursor.count>0){
                try {
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
                }catch (ex:Exception){
                    ex.printStackTrace()
                }

            }
          cursor?.let {
              it.close()
          }


            imageList
        }
    }

    private suspend fun setQuery(context:Context,string: String):ArrayList<ImageModel>{
       return withContext(Dispatchers.IO) {
            val contentResolver: ContentResolver = context.contentResolver
            val list = ArrayList<ImageModel>()
           val uri =Constant.uri
            val selection = MediaStore.Images.Media.DISPLAY_NAME + " like ? "
            val args = arrayOf("%$string%")
            val cursor: Cursor? = contentResolver.query(uri, null, selection, args, null)

            if (cursor != null && cursor.count > 0) {
                try {
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

                        val contentId =
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))


                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentId
                        )

                        val sDate = getCurrentDate(date * 1000)

                        val model = ImageModel()
                        model.path = path
                        model.name = name
                        model.bucketName = bucketName
                        model.height = height
                        model.width = width
                        model.size = size
                        model.date = date
                        model.textDate = sDate
                        model.contentId = contentId
                        model.contentUri = contentUri.toString()
                        model.isDuplicate = false
                        list.add(model)
                    }
                }catch (ex:Exception){
                    ex.printStackTrace()
                }

            }

            cursor?.let {
                it.close()
            }
            list
           }
            }




private fun getCurrentDate(timestamp: Long) :String {
    return SimpleDateFormat("dd MMMM yyyy").format(Date(timestamp)).toString()
}

}