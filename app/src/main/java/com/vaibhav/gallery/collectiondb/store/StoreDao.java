package com.vaibhav.gallery.collectiondb.store;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.Query;
import com.vaibhav.gallery.collectiondb.store.Store;
import com.vaibhav.gallery.model.ImageModel;

import java.util.List;

@Dao
public interface StoreDao {

    @Insert
    void insert(Store store);

    @Query("DELETE FROM store WHERE path=:path")
    void delete(String path);

    @Query("SELECT * FROM store WHERE refID=:ref ORDER BY id DESC")
    LiveData<List<Store>> getLiveData(int ref);
}