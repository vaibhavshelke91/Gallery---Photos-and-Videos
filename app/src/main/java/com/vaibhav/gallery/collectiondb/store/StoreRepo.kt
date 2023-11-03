package com.vaibhav.gallery.collectiondb.store

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StoreRepo (val application: Application) {
    var storeDao: StoreDao

    init {
        val db=StoreDb.getInstance(application)
        storeDao=db.storeDao()

    }
    fun insert(store: Store){
        CoroutineScope(Dispatchers.IO).launch {
            storeDao.insert(store)
        }
    }
    fun delete(store: Store){
        CoroutineScope(Dispatchers.IO).launch {
            store.path?.let {
                storeDao.delete(it)
            }
        }
    }
    fun getLiveData(refId:Int):LiveData<List<Store>>{
        return storeDao.getLiveData(refId)
    }
}