package com.vaibhav.gallery.collectiondb.store;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Store.class},version = 1)
public abstract class StoreDb extends RoomDatabase {
    private static StoreDb instance=null;
    public abstract StoreDao storeDao();
    public synchronized static StoreDb getInstance(Application application){
        if (instance==null){
            instance= Room.databaseBuilder(application.getApplicationContext(),StoreDb.class,"store")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
