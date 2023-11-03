package com.vaibhav.gallery.collectiondb.collection;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Collection.class},version = 1)
public abstract class CollectionDb extends RoomDatabase {
    private static CollectionDb instance=null;
    public abstract CollectionDao collectionDao();
    public static synchronized CollectionDb getInstance(Application application){
        if (instance==null){
            instance= Room.databaseBuilder(application.getApplicationContext(),CollectionDb.class,"collectiondb")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
