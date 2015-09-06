package com.cedetel.android.tigre.db;

import java.io.FileNotFoundException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class FotoDB {
	public static final String KEY_TITLE="title";
    public static final String KEY_TAG="tag";
    public static final String KEY_LAT = "latitud";
    public static final String KEY_LONG = "longitud";
    public static final String KEY_DATE = "date";
    public static final String KEY_ROUTE = "route";
    public static final String KEY_ROWID="_id";
    
  
    private static final String DATABASE_CREATE =
        "create table fotos (_id integer primary key autoincrement, "
            + "title text not null, tag text not null, latitud integer not null, " +
            		"longitud integer not null, date integer not null, route integer not null);";

    private static final String DATABASE_NAME = "dbcamara";
    private static final String DATABASE_TABLE = "fotos";
    private static final int DATABASE_VERSION = 2;

    private SQLiteDatabase mDb;
    private final Context mCtx;

    public FotoDB(Context ctx) {
        this.mCtx = ctx;
    }
    
    public FotoDB open() throws SQLException {
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

    public long createPhoto(String title, String tag, int latitud, int longitud, long date, long route) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_TAG, tag);
        initialValues.put(KEY_LAT, latitud);
        initialValues.put(KEY_LONG, longitud);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_ROUTE, route);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deletePhoto(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllPhotos() {
        return mDb.query(DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_TITLE, KEY_TAG, KEY_LAT, KEY_LONG, KEY_DATE, KEY_ROUTE}, null, null, null, null, null);
    }

    public Cursor fetchPhoto(long rowId) throws SQLException {
        Cursor result = mDb.query(true, DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_TITLE, KEY_TAG, KEY_LAT, KEY_LONG, KEY_DATE, KEY_ROUTE}, KEY_ROWID + "=" + rowId, null, null,
                null, null);
        if ((result.count() == 0) || !result.first()) {
            throw new SQLException("No photo matching ID: " + rowId);
        }
        return result;
    }
    
    public Cursor fetchPhotoByRoute(long idRuta) throws SQLException {
    	Cursor result = mDb.query(true, DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_TITLE, KEY_TAG, KEY_LAT, KEY_LONG, KEY_DATE, KEY_ROUTE}, KEY_ROUTE + "=" + idRuta, null, null,
                null, null);
    	 if ((result.count() == 0) || !result.first()) {
//             throw new SQLException("No photo matching by Route: " + idRuta);
         }
    	return result;
    } 
    
    public boolean updatePhoto(long rowId, String title, String tag) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_TAG, tag);
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}

