package com.mimotic.tigre.model.db.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.mimotic.tigre.common.LogTigre;
import com.mimotic.tigre.model.Foto;
import com.mimotic.tigre.model.Ruta;
import com.mimotic.tigre.model.db.DatabaseManagerLocal;
import com.mimotic.tigre.model.db.LocalSQLiteHelper;

import java.util.ArrayList;

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



    private Foto cursorToFoto(Cursor cursor) {
        Foto foto = new Foto();

        foto.setId(cursor.getInt(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_PHOTOS_ID)));
        foto.setIdRuta(cursor.getInt(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_PHOTOS_ID_RUTA)));
        foto.setIdCoords(cursor.getInt(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_PHOTOS_ID_COORDS)));
        foto.setUrl(cursor.getString(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_PHOTOS_URL)));
        foto.setTimestamp(cursor.getLong(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_PHOTOS_TIMESTAMP)));

        return foto;
    }


    public int createFoto(Foto foto) {
        ContentValues values = new ContentValues();

        values.put(LocalSQLiteHelper.COLUMN_PHOTOS_ID_COORDS, foto.getIdCoords());
        values.put(LocalSQLiteHelper.COLUMN_PHOTOS_ID_RUTA, foto.getIdRuta());
        values.put(LocalSQLiteHelper.COLUMN_PHOTOS_TIMESTAMP, foto.getTimestamp());
        values.put(LocalSQLiteHelper.COLUMN_PHOTOS_URL, foto.getUrl());

        long insertId = database.insertWithOnConflict(
                LocalSQLiteHelper.TABLE_PHOTOS, null, values,
                SQLiteDatabase.CONFLICT_IGNORE);

        return (int)insertId;
    }




    public ArrayList<Foto> getAllPhotos() {
        ArrayList<Foto> fotos = new ArrayList<>();

        String QUERY = "SELECT * FROM photos";
        try{

            Cursor cursor = database.rawQuery(QUERY, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Foto foto = cursorToFoto(cursor);
                fotos.add(foto);
                cursor.moveToNext();
            }

            // Make sure to close the cursor
            cursor.close();



        }catch (Exception e){
            LogTigre.e(TAG, "No hay fotos", e);
        }

        return fotos;
    }



}
