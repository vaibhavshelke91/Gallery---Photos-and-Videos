package com.vaibhav.gallery.collectiondb.store

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class StoreViewModel(application: Application):AndroidViewModel(application) {
    private var storeRepo:StoreRepo

    init {
        storeRepo= StoreRepo(application)
    }
    fun insert(store: Store){
        storeRepo.insert(store)
    }
    fun delete(store: Store){
        storeRepo.delete(store)
    }
    fun getLiveData(refId:Int):LiveData<List<Store>>{
        return storeRepo.getLiveData(refId)
    }
}