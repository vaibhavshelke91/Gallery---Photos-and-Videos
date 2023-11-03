package com.vaibhav.gallery.datastore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SortViewModel :ViewModel() {

    private var liveData:MutableLiveData<String> = MutableLiveData<String>()

    public fun setOrder(string: String){
       liveData.value=string
    }
    public fun getOrder():LiveData<String>{
        return liveData
    }
}