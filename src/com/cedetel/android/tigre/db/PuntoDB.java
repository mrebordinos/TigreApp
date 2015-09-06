package com.cedetel.android.tigre.db;

import java.io.FileNotFoundException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

// BD DE LOS PUNTOS DE INTERES

public class PuntoDB {
	public static final String KEY_TITLE="title";
    public static final String KEY_BODY="body";
    public static final String KEY_LAT = "latitud";
    public static final String KEY_LONG = "longitud";
    public static final String KEY_ALT = "altitud";
    public static final String KEY_DATE = "date";
    public static final String KEY_ICON = "icono";
    public static final String KEY_ROWID="_id";
    public static final String KEY_ROUTE="route";   
  
    private static final String DATABASE_CREATE =
        "create table ipuntos (_id integer primary key autoincrement, "
            + "title text not null, body text not null, latitud integer not null, " +
            		"longitud integer not null, altitud integer not null, date integer not null, icono text not null, route integer not null);";

    private static final String DATABASE_NAME = "dbipuntos";
    private static final String DATABASE_TABLE = "ipuntos";
    private static final int DATABASE_VERSION = 2;

    private SQLiteDatabase mDb;
    private final Context mCtx;

    public PuntoDB(Context ctx) {
        this.mCtx = ctx;
    }
    
    public PuntoDB open() throws SQLException {
        try {
            mDb = mCtx.openDatabase(DATABASE_NAME, null);
        } catch (FileNotFoundException e) {
            try {
                mDb = mCtx.createDatabase(DATABASE_NAME, DATABASE_VERSION, 0, null);
                mDb.execSQL(DATABASE_CREATE);
            } catch (FileNotFoundException e1) {
                throw new SQLException("Could not create database");
            }
        }
        return this;
    }

    public void close() {
        mDb.close();
    }

    public long createIpunto(String title, String body, int latitud, int longitud, int altitud, long date, String icono, long route) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_LAT, latitud);
        initialValues.put(KEY_LONG, longitud);
        initialValues.put(KEY_ALT, altitud);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_ICON, icono);
        initialValues.put(KEY_ROUTE, route);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteIpunto(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllIpuntos() {
        Cursor result = mDb.query(DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_LAT, KEY_LONG, KEY_ALT, KEY_DATE, KEY_ICON, KEY_ROUTE}, null, null, null, null, null);
        return result;
    }

    public Cursor fetchIpunto(long rowId) throws SQLException {
        Cursor result = mDb.query(true, DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_LAT, KEY_LONG, KEY_ALT, KEY_DATE, KEY_ICON, KEY_ROUTE}, KEY_ROWID + "=" + rowId, null, null,
                null, null);
        if ((result.count() == 0) || !result.first()) {
            throw new SQLException("No point matching ID: " + rowId);
        }
        return result;
    }
    
    public Cursor fetchIpuntoByRoute(long idRuta) throws SQLException {
    	Cursor result = mDb.query(true, DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_LAT, KEY_LONG, KEY_ALT, KEY_DATE, KEY_ICON, KEY_ROUTE}, KEY_ROUTE + "=" + idRuta, null, null,
                null, null);
    	 if ((result.count() == 0) || !result.first()) {
//             throw new SQLException("No point matching by Route: " + idRuta);
         }
    	return result;
    } 

    public boolean updateIpuntos(long rowId, String title, String body, String icono) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);
        args.put(KEY_ICON, icono);
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
