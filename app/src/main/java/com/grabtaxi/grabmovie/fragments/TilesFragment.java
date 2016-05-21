package com.grabtaxi.grabmovie.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.common.collect.Lists;
import com.grabtaxi.grabmovie.R;
import com.grabtaxi.grabmovie.adapter.TilesListAdapter;
import com.grabtaxi.grabmovie.db.FavouritesDaoImpl;
import com.grabtaxi.grabmovie.model.MovieInfo;
import com.grabtaxi.grabmovie.adapter.MovieTileRow;
import com.grabtaxi.grabmovie.server.MovieServerApi;
import com.grabtaxi.grabmovie.server.MovieServerEndpoint;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit.RetrofitError;

public class TilesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<TilesListAdapter.Item>> {

    private static final String TAG = TilesFragment.class.getSimpleName();

    private static final int NOW_SHOWING_TILES = 0;
    private static final int FAVOURITES_TILES = 1;
    public static final int MAX_TILES_IN_ROW = 3;
    public static final int MAX_ROWS_TO_PREFETCH = 5;

    private static final String ARG_POSITION = "position";

    private Picasso mPicasso;
    private ListView mTileContent;
    private int mPosition;
    private MovieServerEndpoint mEndpoint;
    private FavouritesDaoImpl mFavouritesDao;


    public static TilesFragment newInstance(int position) {

        TilesFragment frag = new TilesFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView" + mPosition);
        setRetainInstance(true);
        FrameLayout tileContainer = (FrameLayout) inflater.inflate(R.layout.frag_tiles, container, false);
        mTileContent = (ListView) tileContainer.findViewById(R.id.listContent);
        mTileContent.setAdapter(new TilesListAdapter(getContext(), inflater));
        return tileContainer;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPosition = getArguments().getInt(ARG_POSITION);
        getLoaderManager().initLoader(mPosition, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mTileContent.invalidateViews();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPicasso = Picasso.with(getContext());
        MovieServerApi server = new MovieServerApi();
        mEndpoint = server.getEndpoint();
        mFavouritesDao = new FavouritesDaoImpl(getContext());
    }


    @Override
    public Loader<List<TilesListAdapter.Item>> onCreateLoader(final int id, Bundle args) {

        if (id == NOW_SHOWING_TILES || id == FAVOURITES_TILES) {

            Loader<List<TilesListAdapter.Item>> loader = new AsyncTaskLoader<List<TilesListAdapter.Item>>(getActivity()) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();

                    Log.d(TAG, "Loader [" + hashCode() + "] onStartLoading changed["
                            + takeContentChanged() + "] position = " + mPosition);
                    forceLoad();
                }

                @Override
                public List<TilesListAdapter.Item> loadInBackground() {
                    Log.d(TAG, "Loader [" + hashCode() + "] loadInBackground, position = " + mPosition);

                    List<TilesListAdapter.Item> list = Lists.newArrayList();
                    List<MovieInfo> movies = null;
                    if (id == NOW_SHOWING_TILES) {
                        try {
                            movies = mEndpoint.getNowPlayingMovies();
                        } catch (RetrofitError retrofitError) {
                            Log.e(TAG, "Error while trying to get now playing movies", retrofitError);
                        }
                    } else if (id == FAVOURITES_TILES) {
                        movies = mFavouritesDao.getFavouriteMovies();
                    }

                    if (movies == null || movies.size() == 0) {
                        Log.e(TAG, "MovieInfo list is either null or empty");
                        return null;
                    }

                    Log.i(TAG, "MovieInfo list count = " + movies.size());
                    int currRowCapacity = 0;
                    int numRows = 0;
                    List<MovieInfo> currRow = Lists.newArrayListWithCapacity(MAX_TILES_IN_ROW);

                    for (MovieInfo m : movies) {

                        if (isAbandoned()) {
                            Log.d(TAG, "Loader [" + hashCode() + "] is abandoned, position = " + mPosition);
                        }

                        if (id == NOW_SHOWING_TILES && (m.getBackdropPath() == null || m.getBackdropPath().isEmpty()))
                            continue;

                        if (currRowCapacity + 1 <= MAX_TILES_IN_ROW) {
                            currRow.add(m);
                            currRowCapacity++;
                        } else {
                            MovieTileRow row = new MovieTileRow(currRow, mPicasso, TilesFragment.this);
                            currRowCapacity = 0;
                            list.add(row);
                            numRows++;
                            if (numRows <= MAX_ROWS_TO_PREFETCH) {
                                row.prefetchTileImages();
                            }
                            currRow = Lists.newArrayListWithCapacity(MAX_TILES_IN_ROW);
                            currRow.add(m);
                            currRowCapacity++;
                        }

                    }
                    // Adding the last remaining row.
                    list.add(new MovieTileRow(currRow, mPicasso, TilesFragment.this));

                    Log.d(TAG, "Loader [" + hashCode() + "] has ended, position = \" + mPosition");
                    return list;
                }
            };
            return loader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<TilesListAdapter.Item>> loader, List<TilesListAdapter.Item> data) {
        Log.d(TAG, "Loader [" + loader.hashCode() + "] has FINISHED");
        ((TilesListAdapter) mTileContent.getAdapter()).setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<TilesListAdapter.Item>> loader) {
        Log.d(TAG, "Loader [" + loader.hashCode() + "] has RESET");
        ((TilesListAdapter) mTileContent.getAdapter()).setData(null);
    }
}
