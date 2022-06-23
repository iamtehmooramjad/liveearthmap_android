package com.dev175.liveearthmap.myinterface;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.dev175.liveearthmap.model.SavePlace;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface SavePlaceDao {

    @Insert
    void placeInsertion(SavePlace savePlace);

    @Query("SELECT * FROM SavePlace")
    LiveData<List<SavePlace>> getSavedPlaces();


    @Query("DELETE FROM SavePlace")
    void deleteAll();

    @Query("DELETE FROM SavePlace WHERE placeId = :id ")
    void deleteSavePlace(int id);
}
