package com.vaibhav.gallery.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.vaibhav.gallery.model.ImageModel;

import java.util.List;

@Dao
public interface LikedDao {

    @Insert
    void insert(ImageModel liked);

    @Query("SELECT EXISTS(SELECT * FROM liked WHERE path=:string)")
    boolean isLiked(String string);

    @Query("DELETE FROM liked WHERE path=:string")
    void deletePath(String string);

    @Query("DELETE FROM liked")
    void deleteAll();

    @Query("SELECT * FROM liked ORDER BY id DESC")
    LiveData<List<ImageModel>> getAllLiked();
}
