package com.vaibhav.gallery.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.vaibhav.gallery.model.ImageModel

class LikedViewModel(application: Application) :AndroidViewModel(application){

    private var likedRepository:LikedRepository

    init {
        likedRepository= LikedRepository(application)
    }
    public fun insert(imageModel: ImageModel){
        remove(imageModel)
        likedRepository.insert(imageModel)
    }
    public fun remove(imageModel: ImageModel){
        likedRepository.delete(imageModel)
    }
    public fun isLiked(imageModel: ImageModel):Boolean{
        return likedRepository.isLiked(imageModel)
    }
    public fun getLiveData():LiveData<List<ImageModel>>{
        return likedRepository.getLikedLiveData()
    }
}