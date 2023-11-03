package com.vaibhav.gallery.collectiondb.collection

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CollectionDao {

    @Insert
    fun insert(collection: Collection)

    @Query("SELECT * FROM collection ORDER BY id DESC")
    fun getLiveData(): LiveData<List<Collection>>
}