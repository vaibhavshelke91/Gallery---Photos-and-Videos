package com.vaibhav.gallery.collectiondb.collection

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CollectionRepo(private val application: Application) {

    var collectionDao:CollectionDao = CollectionDb.getInstance(application).collectionDao()

    fun insert(collection: Collection){
        CoroutineScope(Dispatchers.IO).launch {
            collectionDao.insert(collection)
        }
    }
    fun getLiveData():LiveData<List<Collection>>{
        return collectionDao.getLiveData()
    }


}