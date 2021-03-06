package com.mimotic.tigre.model.db.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;
import com.mimotic.tigre.common.LogTigre;
import com.mimotic.tigre.model.Foto;
import com.mimotic.tigre.model.IPoint;
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


        if(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_LAT)>0) {
            foto.setLatitude(cursor.getDouble(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_LAT)));
        }

        if(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_LONG)>0) {
            foto.setLongitude(cursor.getDouble(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_LONG)));
        }

        if(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_ALT)>0) {
            foto.setAltitude(cursor.getDouble(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_ALT)));
        }

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



    public ArrayList<LatLng> getAllPhotosCoords() {
        ArrayList<LatLng> fotosCoords = new ArrayList<>();

        String QUERY = "SELECT c.lat, c.long FROM photos as f LEFT JOIN coords as c on c.id = f.id_coords";
        try{

            Cursor cursor = database.rawQuery(QUERY, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                double latitude = cursor.getDouble(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_LAT));
                double longitude = cursor.getDouble(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_LONG));

                LatLng mpoint = new LatLng(latitude, longitude);

                fotosCoords.add(mpoint);
                cursor.moveToNext();
            }

            // Make sure to close the cursor
            cursor.close();



        }catch (Exception e){
            LogTigre.e(TAG, "No hay fotos", e);
        }

        return fotosCoords;
    }



    public ArrayList<Foto> getAllPhotosByRuta(int idRuta) {
        ArrayList<Foto> fotos = new ArrayList<>();

        String QUERY = "SELECT p.id, p.id_coords, p.id_ruta, p.url_photo, p.timestamp, c.lat, c.long, c.alt " +
                "FROM photos as p LEFT JOIN coords as c ON c.id = p.id_coords WHERE p.id_ruta = " + idRuta;

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



    public void deletePhotoById(int id) {
        try{
            database.delete(LocalSQLiteHelper.TABLE_PHOTOS, LocalSQLiteHelper.COLUMN_PHOTOS_ID + " = " + id, null);

            LogTigre.i(TAG, "Eliminadas de base de datos la foto");
        }catch (Exception e){
            LogTigre.e(TAG, "error al borrar foto", e);
        }
    }

}
