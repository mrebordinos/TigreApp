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

public class RutaDataSource {

    private static final String TAG = "RutaDataSource";

    // Database fields
    private SQLiteDatabase database;
    private LocalSQLiteHelper dbHelper;


    public RutaDataSource(Context context) {
        dbHelper = LocalSQLiteHelper.getInstance(context);
        DatabaseManagerLocal.initializeInstance(dbHelper);
    }

    public void open() throws SQLException {
        database = DatabaseManagerLocal.getInstance().openDatabase();
    }

    public void close() {
        DatabaseManagerLocal.getInstance().closeDatabase();
    }


    private Ruta cursorToRuta(Cursor cursor) {
        Ruta ruta = new Ruta();

        ruta.setId(cursor.getInt(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_RUTAS_ID)));
        ruta.setTitle(cursor.getString(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_RUTAS_TITLE)));
        ruta.setDescription(cursor.getString(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_RUTAS_DESCRIPCION)));
        ruta.setTags(cursor.getString(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_RUTAS_TAGS)));
        ruta.setState(cursor.getInt(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_RUTAS_ESTADO)));
        ruta.setTimestampStart(cursor.getLong(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_RUTAS_INICIO)));
        ruta.setTimestampEnd(cursor.getLong(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_RUTAS_FIN)));

        return ruta;
    }

    public int createRuta(Ruta ruta) {
        ContentValues values = new ContentValues();

        values.put(LocalSQLiteHelper.COLUMN_RUTAS_TITLE, ruta.getTitle());
        values.put(LocalSQLiteHelper.COLUMN_RUTAS_DESCRIPCION, ruta.getDescription());
        values.put(LocalSQLiteHelper.COLUMN_RUTAS_TAGS, ruta.getTags());
        values.put(LocalSQLiteHelper.COLUMN_RUTAS_ESTADO, ruta.getState());
        values.put(LocalSQLiteHelper.COLUMN_RUTAS_INICIO, ruta.getTimestampStart());

        long insertId = database.insertWithOnConflict(
                LocalSQLiteHelper.TABLE_RUTAS, null, values,
                SQLiteDatabase.CONFLICT_IGNORE);

        return (int)insertId;
    }



    public void deleteRutaById(int id) {
        try{
            database.delete(LocalSQLiteHelper.TABLE_RUTAS, LocalSQLiteHelper.COLUMN_RUTAS_ID + " = " + id, null);

            database.delete(LocalSQLiteHelper.TABLE_COORDS, LocalSQLiteHelper.COLUMN_COORDS_ID_RUTA + " = " + id, null);

            database.delete(LocalSQLiteHelper.TABLE_POINTS, LocalSQLiteHelper.COLUMN_POINTS_ID_RUTA + " = " + id, null);

            LogTigre.i(TAG, "Eliminadas de base de datos la ruta");
        }catch (Exception e){
            LogTigre.e(TAG, "error al borrar ruta", e);
        }
    }



    public Ruta getRutaById(int id) {
        Ruta ruta = null;

        String QUERY = "SELECT * FROM mis_rutas WHERE id = " + id;
        try{

            Cursor cursor = database.rawQuery(QUERY, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ruta = cursorToRuta(cursor);
                cursor.moveToNext();
            }

            // Make sure to close the cursor
            cursor.close();

            return ruta;

        }catch (Exception e){
            LogTigre.e(TAG, "No hay ruta", e);
            return null;
        }
    }




    public int getIdRutaInProgress() {
        int rutaId = -1;

        String QUERY = "SELECT id FROM mis_rutas WHERE state = 1";
        try{

            Cursor cursor = database.rawQuery(QUERY, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                rutaId = cursor.getInt(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_RUTAS_ID));
                cursor.moveToNext();
            }

            // Make sure to close the cursor
            cursor.close();

            return rutaId;

        }catch (Exception e){
            LogTigre.e(TAG, "No hay ruta", e);
            return rutaId;
        }
    }




    public ArrayList<Ruta> getAllRutas() {
        ArrayList<Ruta> rutas = new ArrayList<>();

        String QUERY = "SELECT * FROM mis_rutas";
        try{

            Cursor cursor = database.rawQuery(QUERY, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Ruta ruta = cursorToRuta(cursor);
                rutas.add(ruta);
                cursor.moveToNext();
            }

            // Make sure to close the cursor
            cursor.close();



        }catch (Exception e){
            LogTigre.e(TAG, "No hay rutas", e);
        }

        return rutas;
    }



    public void finishRuta(int idRuta){
        ContentValues values = new ContentValues();

        values.put(LocalSQLiteHelper.COLUMN_RUTAS_ESTADO, 0);
        values.put(LocalSQLiteHelper.COLUMN_RUTAS_FIN, System.currentTimeMillis());

        database.update(LocalSQLiteHelper.TABLE_RUTAS, values, LocalSQLiteHelper.COLUMN_RUTAS_ID + " = " + idRuta, null);
    }



    public int getLastRuta() {
        int idRuta = 0;

        String QUERY = "SELECT id FROM mis_rutas ORDER BY id DESC LIMIT 1";

        try{

            Cursor cursor = database.rawQuery(QUERY, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                idRuta = cursor.getInt(cursor.getColumnIndex(LocalSQLiteHelper.COLUMN_RUTAS_ID));
                cursor.moveToNext();
            }

            // Make sure to close the cursor
            cursor.close();

            return idRuta;

        }catch (Exception e){
            LogTigre.e(TAG, "No hay ruta", e);
            return idRuta;
        }
    }
}
