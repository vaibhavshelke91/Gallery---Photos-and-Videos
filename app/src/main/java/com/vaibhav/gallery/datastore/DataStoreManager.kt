package com.vaibhav.gallery.datastore

import android.content.Context
import android.provider.MediaStore
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "SORT_DB")

class DataStoreManager(val context: Context) {


    companion object {

        val ORDER = stringPreferencesKey("ORDER")
        val STORE="store"
        val STORE_ALBUM="store_album"
        val STORE_VIDEO="store_video"
        val KEY="key"

        val V_NAME=MediaStore.Video.Media.DISPLAY_NAME+" ASC"
        val V_NAME_REVERSE=MediaStore.Video.Media.DISPLAY_NAME+" DESC"
        val V_LATEST=MediaStore.Video.Media.DATE_ADDED+" DESC"
        val V_OLDEST=MediaStore.Video.Media.DATE_ADDED+" ASC"
        val V_SIZE=MediaStore.Video.Media.SIZE+" DESC"
        val V_SIZE_REVERSE=MediaStore.Video.Media.SIZE+" ASC"

        val NAME= MediaStore.Images.Media.DISPLAY_NAME+" ASC"
        val NAME_REVERSE=MediaStore.Images.Media.DISPLAY_NAME+" DESC"
        val LATEST=MediaStore.Images.Media.DATE_ADDED+" DESC"
        val OLDEST=MediaStore.Images.Media.DATE_ADDED+" ASC"
        val SIZE=MediaStore.Images.Media.SIZE+" DESC"
        val SIZE_REVERSE=MediaStore.Images.Media.SIZE+" ASC"

    }



suspend fun saveToDataStore(string: String) {
    context.dataStore.edit {
        it[ORDER] = string
    }

}
suspend fun getFromDataStore() :String{
    val preferences= context.dataStore.data.first()
    return preferences[ORDER]?: LATEST
}
}