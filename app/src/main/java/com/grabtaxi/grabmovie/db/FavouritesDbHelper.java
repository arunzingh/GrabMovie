package com.grabtaxi.grabmovie.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.grabtaxi.grabmovie.db.FavouriteMovieContract.FavouriteMovieEntry;
import com.google.common.base.Joiner;

public class FavouritesDbHelper extends SQLiteOpenHelper {

    private static final String TAG = FavouritesDbHelper.class.getSimpleName();

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "grabmovie.db";


    public FavouritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createFavouriteTable(db);
    }

    private void createFavouriteTable(SQLiteDatabase db) {
        createTable(db, FavouriteMovieEntry.TABLE_NAME,
                BaseColumns._ID + " INTEGER primary key autoincrement",
                FavouriteMovieEntry.COLUMN_NAME_MOVIE_ID + " TEXT UNIQUE",
                FavouriteMovieEntry.COLUMN_NAME_POSTER_IMG_PATH + " TEXT" );
    }

    private static void createTable(SQLiteDatabase db,
                                    String tableName, String... columns) {
        db.execSQL("CREATE TABLE " + tableName
                + " ( " + Joiner.on(", ").join(columns) + " )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
