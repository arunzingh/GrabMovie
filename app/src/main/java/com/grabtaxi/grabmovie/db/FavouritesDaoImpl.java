package com.grabtaxi.grabmovie.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.common.collect.Lists;
import com.grabtaxi.grabmovie.db.FavouriteMovieContract.FavouriteMovieEntry;
import com.grabtaxi.grabmovie.model.MovieInfo;

import java.util.List;

public class FavouritesDaoImpl {

    private static final String TAG = FavouritesDaoImpl.class.getSimpleName();

    protected final Context mContext;
    private final FavouritesDbHelper mDbHelper;

    public FavouritesDaoImpl(Context context) {
        mContext = context;
        mDbHelper = new FavouritesDbHelper(context);
    }

    public long addFavouriteMovie(int movieId, String posterPath) {

        ContentValues cvRow = new ContentValues();
        cvRow.put(FavouriteMovieEntry.COLUMN_NAME_MOVIE_ID, movieId);
        cvRow.put(FavouriteMovieEntry.COLUMN_NAME_POSTER_IMG_PATH, posterPath);
        SQLiteDatabase db = null;
        long id = 0;
        try {
            db = mDbHelper.getWritableDatabase();
            id = db.insert(FavouriteMovieEntry.TABLE_NAME, null, cvRow);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen()) db.close();
        }

        return id;
    }

    public int removeFavaouriteMovie(int movieId) {
        String selection = FavouriteMovieEntry.COLUMN_NAME_MOVIE_ID + " = ? ";
        String[] args = {String.valueOf(movieId)};
        SQLiteDatabase db = null;
        int count = 0;
        try {
            db = mDbHelper.getWritableDatabase();
            count = db.delete(FavouriteMovieEntry.TABLE_NAME, selection, args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen()) db.close();

        }

        return count;
    }

    public boolean isFavouriteMovie(int movieId) {
        SQLiteDatabase db = null;
        String[] projection = {
                FavouriteMovieEntry._ID,
                FavouriteMovieEntry.COLUMN_NAME_MOVIE_ID,
                FavouriteMovieEntry.COLUMN_NAME_POSTER_IMG_PATH
        };
        String where = FavouriteMovieEntry.COLUMN_NAME_MOVIE_ID + " = ?";
        String[] args = {String.valueOf(movieId)};


        Cursor cursor = null;

        try {
            db = mDbHelper.getReadableDatabase();
            cursor = db.query(
                    FavouriteMovieEntry.TABLE_NAME, projection, where, args, null, null, null);

            if (cursor != null) {
               return cursor.getCount() == 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db.isOpen()) db.close();
        }

        return false;
    }

    public List<MovieInfo> getFavouriteMovies() {
        SQLiteDatabase db = null;
        String[] projection = {
                FavouriteMovieEntry._ID,
                FavouriteMovieEntry.COLUMN_NAME_MOVIE_ID,
                FavouriteMovieEntry.COLUMN_NAME_POSTER_IMG_PATH
        };

        String sortOrder = FavouriteMovieEntry.COLUMN_NAME_MOVIE_ID + " ASC";
        Cursor cursor = null;
        List<MovieInfo> favMovies = Lists.newArrayList();
        try {
            db = mDbHelper.getReadableDatabase();
            cursor = db.query(
                    FavouriteMovieEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int movieId = cursor.getInt(cursor.getColumnIndexOrThrow(FavouriteMovieEntry.COLUMN_NAME_MOVIE_ID));
                    String posterPath = cursor.getString(cursor.getColumnIndexOrThrow(FavouriteMovieEntry.COLUMN_NAME_POSTER_IMG_PATH));
                    MovieInfo movie = new MovieInfo(movieId, posterPath);
                    favMovies.add(movie);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db.isOpen()) db.close();
        }

        return favMovies;
    }
}
