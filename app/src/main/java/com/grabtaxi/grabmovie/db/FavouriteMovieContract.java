package com.grabtaxi.grabmovie.db;


import android.provider.BaseColumns;

public class FavouriteMovieContract {

    public static abstract class FavouriteMovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "favourite";
        public static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        public static final String COLUMN_NAME_POSTER_IMG_PATH = "poster_image_path";
    }
}
