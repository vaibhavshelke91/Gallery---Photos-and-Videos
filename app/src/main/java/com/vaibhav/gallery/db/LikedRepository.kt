package com.vaibhav.gallery.db

import android.app.Application
import androidx.lifecycle.LiveData
import com.vaibhav.gallery.model.ImageModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LikedRepository(private val application: Application) {
    var likedDao: LikedDao
    private var liveData: LiveData<List<ImageModel>>

    init {
        val database=LikedDatabase.getInstance(application)
        likedDao=database.likedDao()
        liveData=likedDao.allLiked
    }

    fun insert(imageModel: ImageModel){
        CoroutineScope(Dispatchers.IO).launch {
            likedDao.insert(imageModel)
        }
    }

    fun delete(imageModel: ImageModel){
        CoroutineScope(Dispatchers.IO).launch {
            likedDao.deletePath(imageModel.path)
        }
    }

    fun isLiked(imageModel: ImageModel):Boolean{
        return likedDao.isLiked(imageModel.path)
    }

    fun getLikedLiveData():LiveData<List<ImageModel>>{
        return liveData
    }
}