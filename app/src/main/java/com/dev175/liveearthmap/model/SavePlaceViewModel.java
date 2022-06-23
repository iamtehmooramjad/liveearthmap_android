package com.dev175.liveearthmap.model;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.dev175.liveearthmap.myinterface.SavePlaceDao;
import com.dev175.liveearthmap.utils.EarthMapDatabase;

import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SavePlaceViewModel extends AndroidViewModel {

    SavePlaceDao savePlaceDao;
    private ExecutorService executorService;

    public SavePlaceViewModel(@Nullable Application application) {
        super(application);
        savePlaceDao = EarthMapDatabase.getInstance(application).savePlaceDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<SavePlace>> getAllData() {
        return savePlaceDao.getSavedPlaces();
    }

    public void insert(SavePlace savePlace) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                savePlaceDao.placeInsertion(savePlace);
            }
        });
    }

    public void delete(int id)
    {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                savePlaceDao.deleteSavePlace(id);
            }
        });
    }


}
