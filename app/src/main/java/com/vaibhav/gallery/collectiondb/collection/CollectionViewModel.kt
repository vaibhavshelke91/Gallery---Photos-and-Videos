package com.vaibhav.gallery.collectiondb.collection

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class CollectionViewModel(application: Application):AndroidViewModel(application) {
    var repo=CollectionRepo(application)
    fun insert(collection: Collection){repo.insert(collection)}
    fun getLiveData():LiveData<List<Collection>>{return repo.getLiveData()}
}