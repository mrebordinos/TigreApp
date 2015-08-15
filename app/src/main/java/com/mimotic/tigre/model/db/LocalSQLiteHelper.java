package com.mimotic.tigre.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalSQLiteHelper extends SQLiteOpenHelper {

    // //////////////////// RUTAS ////////////////////////
    public static final String TABLE_RUTAS = "mis_rutas";
    public static final String COLUMN_RUTAS_ID = "id";
    public static final String COLUMN_RUTAS_TITLE = "title";
    public static final String COLUMN_RUTAS_ESTADO = "state";
    public static final String COLUMN_RUTAS_INICIO = "timestamp_start";
    public static final String COLUMN_RUTAS_FIN = "timestamp_end";

    // //////////////////// COORDS ////////////////////////
    public static final String TABLE_COORDS = "coords";
    public static final String COLUMN_COORDS_ID = "id";
    public static final String COLUMN_COORDS_ID_RUTA = "id_ruta";
    public static final String COLUMN_COORDS_LAT = "lat";
    public static final String COLUMN_COORDS_LONG = "long";
    public static final String COLUMN_COORDS_ALT = "alt";

    // //////////////////// PHOTOS ////////////////////////
    public static final String TABLE_PHOTOS = "photos";
    public static final String COLUMN_PHOTOS_ID = "id";
    public static final String COLUMN_PHOTOS_ID_RUTA = "id_ruta";
    public static final String COLUMN_PHOTOS_ID_COORDS = "id_coords";
    public static final String COLUMN_PHOTOS_URL = "url_photo";
    public static final String COLUMN_PHOTOS_TIMESTAMP = "timestamp";


    private static final String DATABASE_CREATE_TABLE_RUTAS = "create table if not exists "
            + TABLE_RUTAS
            + "("
            + COLUMN_RUTAS_ID
            + " integer primary key autoincrement, "
            + COLUMN_RUTAS_TITLE
            + " text not null, "
            + COLUMN_RUTAS_ESTADO
            + " integer not null, "
            + COLUMN_RUTAS_INICIO
            + " integer, "
            + COLUMN_RUTAS_FIN
            + " integer);";


    private static final String DATABASE_CREATE_TABLE_COORDS = "create table if not exists "
            + TABLE_COORDS
            + "("
            + COLUMN_COORDS_ID
            + " integer primary key autoincrement, "
            + COLUMN_COORDS_ID_RUTA
            + " integer not null, "
            + COLUMN_COORDS_LAT
            + " real not null, "
            + COLUMN_COORDS_LONG
            + " real not null, "
            + COLUMN_COORDS_ALT
            + " real);";


    private static final String DATABASE_CREATE_TABLE_PHOTOS = "create table if not exists "
            + TABLE_PHOTOS
            + "("
            + COLUMN_PHOTOS_ID
            + " integer primary key autoincrement, "
            + COLUMN_PHOTOS_ID_RUTA
            + " integer not null, "
            + COLUMN_PHOTOS_ID_COORDS
            + " integer not null, "
            + COLUMN_PHOTOS_URL
            + " text not null, "
            + COLUMN_PHOTOS_TIMESTAMP
            + " integer);";



    private static final String DATABASE_NAME = "rutas.db";

    public static final int DATABASE_VERSION = 1;

    private static LocalSQLiteHelper mInstance = null;

    public static synchronized LocalSQLiteHelper getInstance(final Context ctx){
        if (mInstance == null) {
            mInstance = new LocalSQLiteHelper(ctx);
        }
        return mInstance;
    }

    public LocalSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_TABLE_RUTAS);
        db.execSQL(DATABASE_CREATE_TABLE_COORDS);
        db.execSQL(DATABASE_CREATE_TABLE_PHOTOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUTAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
        db.setVersion(newVersion);
        onCreate(db);
    }

}
