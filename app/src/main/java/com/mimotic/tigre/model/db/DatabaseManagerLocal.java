package com.mimotic.tigre.model.db;

import android.database.sqlite.SQLiteDatabase;

import com.mimotic.tigre.common.LogTigre;

import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseManagerLocal {

    private AtomicInteger mOpenCounter = new AtomicInteger();

    private static DatabaseManagerLocal instance;
    private static LocalSQLiteHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;


    public static synchronized void initializeInstance(LocalSQLiteHelper helper) {
        if (instance == null) {
            LogTigre.e("localDDBB", "initializeInstance");
            instance = new DatabaseManagerLocal();
            mDatabaseHelper = helper;
        }
    }

    public static synchronized DatabaseManagerLocal getInstance() {
        if (instance == null) {
            LogTigre.e("localDDBB", "La instancia de DatabaseManager es null");
            throw new IllegalStateException(DatabaseManagerLocal.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if(mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        LogTigre.d("localDDBB", "openDatabase -> " + mOpenCounter);
        return mDatabase;
    }


    public synchronized void closeDatabase() {
        int miNum = mOpenCounter.decrementAndGet();

        if(miNum == 0) {
            // Closing database
            mDatabase.close();
        }else if(miNum < 0){
            mOpenCounter = new AtomicInteger();
        }
        LogTigre.d("localDDBB", "closeDatabase -> " +  mOpenCounter);
    }

}
