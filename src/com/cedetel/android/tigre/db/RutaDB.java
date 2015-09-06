package com.cedetel.android.tigre.db;

import java.io.FileNotFoundException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

// BD DE LAS RUTAS 

public class RutaDB {
	public static final String KEY_TITLE="title";
    public static final String KEY_BODY="body";
    public static final String KEY_TAG="tag";
    public static final String KEY_COLOR="color";
    public static final String KEY_ROWID="_id";
    
  
    private static final String DATABASE_CREATE =
        "create table rutas (_id integer primary key autoincrement, "
            + "title text not null, body text not null, tag text not null, color text not null);";

    private static final String DATABASE_NAME = "dbtigrutas";
    private static final String DATABASE_TABLE = "rutas";
    private static final int DATABASE_VERSION = 2;

    private SQLiteDatabase mDb;
    private final Context mCtx;

    public RutaDB(Context ctx) {
        this.mCtx = ctx;
    }
    
    public RutaDB open() throws SQLException {
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

    public long createRoute(String title, String body, String tag, String color) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_TAG, tag);
        initialValues.put(KEY_COLOR, color);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteRoute(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllRoutes() {
        return mDb.query(DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_TAG, KEY_COLOR}, null, null, null, null, null);
    }

    public Cursor fetchRoute(long rowId) throws SQLException {
        Cursor result = mDb.query(true, DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_TAG, KEY_COLOR}, KEY_ROWID + "=" + rowId, null, null,
                null, null);
        if ((result.count() == 0) || !result.first()) {
            throw new SQLException("No route matching ID: " + rowId);
        }
        return result;
    }

    public boolean updateRoute(long rowId, String title, String body, String tag) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);
        args.put(KEY_TAG, tag);
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}

