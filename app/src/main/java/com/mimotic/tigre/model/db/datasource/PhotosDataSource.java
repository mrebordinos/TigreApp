package com.mimotic.tigre.model.db.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.mimotic.tigre.model.GeoPunto;
import com.mimotic.tigre.model.db.DatabaseManagerLocal;
import com.mimotic.tigre.model.db.LocalSQLiteHelper;

public class PhotosDataSource {

    private static final String TAG = "PuntosDataSource";

    // Database fields
    private SQLiteDatabase database;
    private LocalSQLiteHelper dbHelper;


    public PhotosDataSource(Context context) {
        dbHelper = LocalSQLiteHelper.getInstance(context);
        DatabaseManagerLocal.initializeInstance(dbHelper);
    }

    public void open() throws SQLException {
        database = DatabaseManagerLocal.getInstance().openDatabase();
    }

    public void close() {
        DatabaseManagerLocal.getInstance().closeDatabase();
    }



}
