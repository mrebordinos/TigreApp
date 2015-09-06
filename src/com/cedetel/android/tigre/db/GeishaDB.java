package com.cedetel.android.tigre.db;

import java.io.FileNotFoundException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class GeishaDB {
	
	public static final String KEY_TITLE="title";
    public static final String KEY_ROWID="_id";
    
  
    private static final String DATABASE_CREATE =
        "create table rutas (_id integer primary key, "
            + "title text not null);";

    private static final String DATABASE_NAME = "dbgeisha";
    private static final String DATABASE_TABLE = "rutas";
    private static final int DATABASE_VERSION = 2;

    private SQLiteDatabase mDb;
    private final Context mCtx;

    public GeishaDB(Context ctx) {
        this.mCtx = ctx;
    }
    
    public GeishaDB open() throws SQLException {
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

    public long createRoute(long rowId, String title) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, rowId);
        initialValues.put(KEY_TITLE, title);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteRoute(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean deleteAllRoutes() {
        return mDb.delete(DATABASE_TABLE, null, null) > 0;
    }
    
    public Cursor fetchAllRoutes() {
        return mDb.query(DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_TITLE}, null, null, null, null, null);
    }

    public Cursor fetchRoute(long rowId) throws SQLException {
        Cursor result = mDb.query(true, DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_TITLE}, KEY_ROWID + "=" + rowId, null, null,
                null, null);
        if ((result.count() == 0) || !result.first()) {
            throw new SQLException("No geisha route matching ID: " + rowId);
        }
        return result;
    }

    public boolean updateRoute(long rowId, String title) {
        ContentValues args = new ContentValues();
//        args.put(KEY_ROWID, rowId);
        args.put(KEY_TITLE, title);
//        return mDb.update(DATABASE_TABLE, args, null, null) > 0;
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
