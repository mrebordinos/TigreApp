package com.mimotic.tigre.model.db.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;
import com.mimotic.tigre.common.LogTigre;
import com.mimotic.tigre.model.IPoint;
import com.mimotic.tigre.model.db.DatabaseManagerLocal;
import com.mimotic.tigre.model.db.LocalSQLiteHelper;

import java.util.ArrayList;

public class IPointsDataSource {

    private static final String TAG = "IPointsDataSource";

    // Database fields
    private SQLiteDatabase database;
    private LocalSQLiteHelper dbHelper;


    public IPointsDataSource(Context context) {
        dbHelper = LocalSQLiteHelper.getInstance(context);
        DatabaseManagerLocal.initializeInstance(dbHelper);
    }

    public void open() throws SQLException {
        database = DatabaseManagerLocal.getInstance().openDatabase();
    }

    public void close() {
        DatabaseManagerLocal.getInstance().closeDatabase();
    }



    private IPoint cursorToIPoint(Cursor cursor) {
        IPoint poi = new IPoint();

        poi.setId(cursor.getInt(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_PHOTOS_ID)));
        poi.setIdRuta(cursor.getInt(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_POINTS_ID_RUTA)));
        poi.setIdCoords(cursor.getInt(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_POINTS_ID_COORDS)));
        poi.setTexto(cursor.getString(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_POINTS_TEXT)));
        poi.setTimestamp(cursor.getLong(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_POINTS_TIMESTAMP)));

        if(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_LAT)>0) {
            poi.setLatitude(cursor.getDouble(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_LAT)));
        }

        if(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_LONG)>0) {
            poi.setLongitude(cursor.getDouble(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_LONG)));
        }

        if(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_ALT)>0) {
            poi.setAltitude(cursor.getDouble(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_ALT)));
        }

        return poi;
    }


    public int createIPoint(IPoint poi) {
        ContentValues values = new ContentValues();

        values.put(LocalSQLiteHelper.COLUMN_POINTS_ID_COORDS, poi.getIdCoords());
        values.put(LocalSQLiteHelper.COLUMN_POINTS_ID_RUTA, poi.getIdRuta());
        values.put(LocalSQLiteHelper.COLUMN_POINTS_TIMESTAMP, poi.getTimestamp());
        values.put(LocalSQLiteHelper.COLUMN_POINTS_TEXT, poi.getTexto());

        long insertId = database.insertWithOnConflict(
                LocalSQLiteHelper.TABLE_POINTS, null, values,
                SQLiteDatabase.CONFLICT_IGNORE);

        return (int)insertId;
    }




    public ArrayList<IPoint> getAllIPointByRuta(int idRuta) {
        ArrayList<IPoint> pois = new ArrayList<>();

        String QUERY = "SELECT p.id, p.id_coords, p.id_ruta, p.content, p.timestamp, c.lat, c.long, c.alt " +
                "FROM points as p LEFT JOIN coords as c ON c.id = p.id_coords WHERE p.id_ruta = " + idRuta;

//        String QUERY = "SELECT * FROM points WHERE id_ruta = " + idRuta;
        try{
            Cursor cursor = database.rawQuery(QUERY, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                IPoint poi = cursorToIPoint(cursor);
                pois.add(poi);
                cursor.moveToNext();
            }

            // Make sure to close the cursor
            cursor.close();

        }catch (Exception e){
            LogTigre.e(TAG, "No hay pois", e);
        }

        return pois;
    }



//    public ArrayList<LatLng> getAllIPointsCoords() {
//        ArrayList<LatLng> poisCoords = new ArrayList<>();
//
//        String QUERY = "SELECT c.lat, c.long FROM points as p LEFT JOIN coords as c on c.id = p.id_coords";
//        try{
//
//            Cursor cursor = database.rawQuery(QUERY, null);
//
//            cursor.moveToFirst();
//            while (!cursor.isAfterLast()) {
//
//                double latitude = cursor.getDouble(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_LAT));
//                double longitude = cursor.getDouble(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_LONG));
//
//                LatLng mpoint = new LatLng(latitude, longitude);
//
//                poisCoords.add(mpoint);
//                cursor.moveToNext();
//            }
//
//            // Make sure to close the cursor
//            cursor.close();
//
//        }catch (Exception e){
//            LogTigre.e(TAG, "No hay pois", e);
//        }
//
//        return poisCoords;
//    }



}
