package com.dev175.liveearthmap.utils;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.dev175.liveearthmap.model.SavePlace;
import com.dev175.liveearthmap.myinterface.SavePlaceDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {SavePlace.class}, version = 2, exportSchema = false)
public abstract class EarthMapDatabase extends RoomDatabase {

    // Data base Instance
    private static volatile EarthMapDatabase INSTANCE;

    //Dao
    public abstract SavePlaceDao savePlaceDao();

    private static final int NUMBER_OF_THREADS = 4;

    //Executor
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Get Database Instance
    public static EarthMapDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (EarthMapDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            EarthMapDatabase.class, "earthMap_db")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /*Override the onCreate method to populate the database.
    For this sample, we clear the database every time it is created.*/
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                SavePlaceDao dao = INSTANCE.savePlaceDao();
                dao.deleteAll();

            });
        }
    };
}
