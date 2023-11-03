package com.vaibhav.gallery.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.vaibhav.gallery.collectiondb.store.Store


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "GRID_DB")
private val Context.dataStoreFotTitle:DataStore<Preferences> by preferencesDataStore(name = "TITLE_DB")


     object StoreOjb
    {
        val ORDER = intPreferencesKey("ORDER")
        val TITLE_VISIBILITY= booleanPreferencesKey("TITLE_VISIBILITY")
    }

    suspend fun Context.saveGridStoreState(size: Int) {
        dataStore.edit {
            it[StoreOjb.ORDER] = size
        }

    }
suspend fun Context.saveTitleState(options: Boolean){
    dataStoreFotTitle.edit {
        it[StoreOjb.TITLE_VISIBILITY]=options
    }
}

fun Context.getTitleStateLiveData():LiveData<Preferences>{
    return dataStoreFotTitle.data.asLiveData()
}

fun Context.getGridStoreLiveData() : LiveData<Preferences>{
    return dataStore.data.asLiveData()
}
