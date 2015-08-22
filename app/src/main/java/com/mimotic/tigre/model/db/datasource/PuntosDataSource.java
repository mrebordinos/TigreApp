package com.mimotic.tigre.model.db.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.mimotic.tigre.common.LogTigre;
import com.mimotic.tigre.model.GeoPunto;
import com.mimotic.tigre.model.Ruta;
import com.mimotic.tigre.model.db.DatabaseManagerLocal;
import com.mimotic.tigre.model.db.LocalSQLiteHelper;

import java.util.ArrayList;

public class PuntosDataSource {

    private static final String TAG = "PuntosDataSource";

    // Database fields
    private SQLiteDatabase database;
    private LocalSQLiteHelper dbHelper;


    public PuntosDataSource(Context context) {
        dbHelper = LocalSQLiteHelper.getInstance(context);
        DatabaseManagerLocal.initializeInstance(dbHelper);
    }

    public void open() throws SQLException {
        database = DatabaseManagerLocal.getInstance().openDatabase();
    }

    public void close() {
        DatabaseManagerLocal.getInstance().closeDatabase();
    }


    private GeoPunto cursorToGeoPunto(Cursor cursor) {
        GeoPunto punto = new GeoPunto();

        punto.setId(cursor.getInt(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_ID)));
        punto.setIdRuta(cursor.getInt(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_ID_RUTA)));
        punto.setLatitude(cursor.getDouble(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_LAT)));
        punto.setLongitude(cursor.getDouble(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_LONG)));
        punto.setAltitude(cursor.getDouble(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_COORDS_ALT)));

        return punto;
    }



    public int insertPoint(GeoPunto punto) {
        ContentValues values = new ContentValues();

        values.put(LocalSQLiteHelper.COLUMN_COORDS_ID_RUTA, punto.getIdRuta());
        values.put(LocalSQLiteHelper.COLUMN_COORDS_LAT, punto.getLatitude());
        values.put(LocalSQLiteHelper.COLUMN_COORDS_LONG, punto.getLongitude());
        values.put(LocalSQLiteHelper.COLUMN_COORDS_ALT, punto.getAltitude());

        long insertId = database.insertWithOnConflict(
                LocalSQLiteHelper.TABLE_COORDS, null, values,
                SQLiteDatabase.CONFLICT_IGNORE);

        return (int)insertId;
    }



    public ArrayList<GeoPunto> getPuntosByRutaId(int idRuta){
        ArrayList<GeoPunto> puntos = new ArrayList<>();

        String QUERY = "SELECT * FROM coords WHERE id_ruta = " + idRuta;
        try{

            Cursor cursor = database.rawQuery(QUERY, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                GeoPunto punto = cursorToGeoPunto(cursor);
                puntos.add(punto);
                cursor.moveToNext();
            }

            // Make sure to close the cursor
            cursor.close();

        }catch (Exception e){
            LogTigre.e(TAG, "Error devolviendo puntos", e);
        }


        return puntos;
    }




}
