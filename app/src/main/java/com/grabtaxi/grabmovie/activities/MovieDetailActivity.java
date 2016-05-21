package com.grabtaxi.grabmovie.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.grabtaxi.grabmovie.R;
import com.grabtaxi.grabmovie.fragments.MovieDetailFragment;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    public static final String EXTRA_MOVIE_ID = "extra_movie_id";
    private static final String TAG_MOVIE_DETAIL_FRAGMENT = "movie_detail_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        setTitle("");
        int movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);

        if (movieId == -1) {
            Log.e(TAG, "Invalid movie id");
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        MovieDetailFragment fragment = (MovieDetailFragment) fm.findFragmentByTag(TAG_MOVIE_DETAIL_FRAGMENT);
        if (fragment == null){
            Log.i(TAG, "Creating detail fragment");
            fragment = MovieDetailFragment.newInstance(movieId);
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.detail_container, fragment);
            ft.commit();
        }

    }

}
