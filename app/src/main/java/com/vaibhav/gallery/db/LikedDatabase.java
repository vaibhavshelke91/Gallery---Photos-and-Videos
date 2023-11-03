package com.vaibhav.gallery.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.vaibhav.gallery.model.ImageModel;

@Database(entities = {ImageModel.class},version = 3)
public abstract class LikedDatabase extends RoomDatabase {

    private static LikedDatabase instance=null;

    public abstract LikedDao likedDao();

    public static synchronized LikedDatabase getInstance(Context context){
        if(instance==null){
            instance= Room.databaseBuilder(context.getApplicationContext()
                            ,LikedDatabase.class,"liked_database").fallbackToDestructiveMigration()
                    .build();
        }

        return instance;

    }
    public static LikedDatabase getNullOrInstance(){
        return instance;
    }
}