package com.grabtaxi.grabmovie.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.grabtaxi.grabmovie.R;
import com.grabtaxi.grabmovie.activities.MovieDetailActivity;
import com.grabtaxi.grabmovie.adapter.MovieTileRow;
import com.grabtaxi.grabmovie.adapter.TilesListAdapter;
import com.grabtaxi.grabmovie.db.FavouritesDaoImpl;
import com.grabtaxi.grabmovie.model.MovieDetailInfo;
import com.grabtaxi.grabmovie.model.MovieInfo;
import com.grabtaxi.grabmovie.server.MovieServerApi;
import com.grabtaxi.grabmovie.server.MovieServerEndpoint;
import com.grabtaxi.grabmovie.util.ColorUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import java.util.List;

import retrofit.RetrofitError;

public class MovieDetailFragment extends Fragment {

    private static final String TAG = MovieDetailFragment.class.getSimpleName();

    private static final String ARG_MOVIE_ID = "movie_id";

    private int mMoiveId = 0;
    private MovieDetailInfo mMovieDetailInfo;
    private List<MovieInfo> mSimilarMovies;
    private MovieServerEndpoint mEndpoint;
    private Picasso mPicasso;
    private FavouritesDaoImpl mFavouritesDao;

    public static MovieDetailFragment newInstance(int movieId) {
        MovieDetailFragment frag = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MOVIE_ID, movieId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        MovieServerApi server = new MovieServerApi();
        mEndpoint = server.getEndpoint();
        mPicasso = Picasso.with(getContext());
        mFavouritesDao = new FavouritesDaoImpl(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_movie_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMoiveId = getArguments().getInt(ARG_MOVIE_ID);
        setFavouriteToggleListener();
        new GetMovieDetailTask().execute(mMoiveId);
        new GetSimilarMoviesTask().execute(mMoiveId);
    }

    class GetMovieDetailTask extends AsyncTask<Integer, Integer, MovieDetailInfo> {

        @Override
        protected MovieDetailInfo doInBackground(Integer... params) {
            MovieDetailInfo movieInfo = null;
            try {
                movieInfo = mEndpoint.getMovieDetailInfo(params[0]);
            } catch (RetrofitError retrofitError) {
                Log.e(TAG, "Error while getting movie details", retrofitError);
            }

            return movieInfo;
        }

        @Override
        protected void onPostExecute(MovieDetailInfo movieDetailInfo) {
            super.onPostExecute(movieDetailInfo);
            mMovieDetailInfo = movieDetailInfo;
            getActivity().setTitle(movieDetailInfo.getTitle());
            setFragmentDetails(movieDetailInfo);
        }
    }

    class GetSimilarMoviesTask extends AsyncTask<Integer, Integer, List<MovieInfo>> {

        @Override
        protected List<MovieInfo> doInBackground(Integer... params) {
            List<MovieInfo> similarMovies = null;
            try {
                similarMovies = mEndpoint.getRelatedMovies(params[0]);
            } catch (RetrofitError retrofitError) {
                Log.e(TAG, "Error while getting movie details", retrofitError);
            }

            if (similarMovies == null || similarMovies.size() == 0) {
                Log.e(TAG, "Related movies list is either null or empty");
                MovieDetailFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideSimilarViews();
                    }
                });
                return null;
            }

            Log.i(TAG, "Related movies list count = " + similarMovies.size());


            Log.d(TAG, "Loader [" + hashCode() + "] has ended, position = \" + mPosition");
            return similarMovies;
        }

        @Override
        protected void onPostExecute(List<MovieInfo> similarMovies) {
            super.onPostExecute(similarMovies);

            if (similarMovies == null || similarMovies.size() == 0) return;

            View view = getView();
            if (view != null) {
                ImageView imgVwSim1 = (ImageView) view.findViewById(R.id.imgSimilar1);
                ImageView imgVwSim2 = (ImageView) view.findViewById(R.id.imgSimilar2);
                ImageView imgVwSim3 = (ImageView) view.findViewById(R.id.imgSimilar3);
                setSimilarMovie(imgVwSim1, similarMovies.get(0));
                setSimilarMovie(imgVwSim2, similarMovies.get(1));
                setSimilarMovie(imgVwSim3, similarMovies.get(2));
            }

        }
    }

    private void hideSimilarViews() {
        View view = getView();
        if (view != null) {
            ((TextView) view.findViewById(R.id.txtSimilar)).setVisibility(View.GONE);
            ((ImageView) view.findViewById(R.id.imgSimilar1)).setVisibility(View.GONE);;
            ((ImageView) view.findViewById(R.id.imgSimilar2)).setVisibility(View.GONE);;
            ((ImageView) view.findViewById(R.id.imgSimilar3)).setVisibility(View.GONE);;
        }

    }

    private void setSimilarMovie(ImageView imgViewSimiar, final MovieInfo movieInfo) {

        if (movieInfo == null) return;

        mPicasso.load(MovieTileRow.IMAGE_BASE_URL + movieInfo.getPosterPath())
                .placeholder(R.drawable.tile_img_placeholder)
                .into(imgViewSimiar);
        imgViewSimiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchDetail = new Intent(getContext(), MovieDetailActivity.class);
                launchDetail.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movieInfo.getId());
                getContext().startActivity(launchDetail);
            }
        });
    }


    private void setFragmentDetails(MovieDetailInfo movieInfo) {
        if (movieInfo == null) {
            Log.e(TAG, "Movie details not found");
            return;
        }

        ImageView imgCover = (ImageView) getView().findViewById(R.id.coverImage);
        final ImageView imgPoster = (ImageView) getView().findViewById(R.id.posterImage);

        loadImage(imgCover, MovieTileRow.IMAGE_BASE_URL + movieInfo.getBackdropPath());
        loadImage(imgPoster, MovieTileRow.IMAGE_BASE_URL + movieInfo.getPosterPath());

        mPicasso.load(MovieTileRow.IMAGE_BASE_URL + movieInfo.getPosterPath())
                .placeholder(R.drawable.tile_img_placeholder)
                .into(imgPoster, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) imgPoster.getDrawable()).getBitmap();
                        Palette palette = Palette.from(bitmap).generate();
                        applyPalette(palette);
                    }

                    @Override
                    public void onError() {

                    }
                });

        View view = getView();
        if (view != null) {
            ((TextView) view.findViewById(R.id.titleTextView)).setText(movieInfo.getTitle());
            ((TextView) view.findViewById(R.id.subtitleTextView)).setText(movieInfo.getTagline());
            ((TextView) view.findViewById(R.id.valRuntime)).setText(movieInfo.getRuntime());
            ((TextView) view.findViewById(R.id.valBudget)).setText(movieInfo.getBudget());
            ((TextView) view.findViewById(R.id.valRelease)).setText(movieInfo.getReleaseDate());
            ((TextView) view.findViewById(R.id.txtOverview)).setText(movieInfo.getOverview());
            ((TextView) view.findViewById(R.id.valGenres)).setText(Joiner.on(", ").join(movieInfo.getGenres()));
        }


    }

    private void applyPalette(Palette palette) {
        if (palette != null) {

            Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
            Palette.Swatch darkVibrantSwatch = palette.getDarkVibrantSwatch();
            Palette.Swatch lightVibrantSwatch = palette.getLightVibrantSwatch();

            Palette.Swatch mutedSwatch = palette.getMutedSwatch();
            Palette.Swatch darkMutedSwatch = palette.getDarkMutedSwatch();
            Palette.Swatch lightMutedSwatch = palette.getLightMutedSwatch();

            if (lightVibrantSwatch != null) {
                applySwatch(darkVibrantSwatch != null ? darkVibrantSwatch : darkMutedSwatch);
            } else if (lightMutedSwatch != null) {
                applySwatch(darkMutedSwatch != null ? darkMutedSwatch : darkVibrantSwatch);
            }
        }
    }


    private void applySwatch(Palette.Swatch darkSwatch) {

        if (darkSwatch == null) {
            // If the palette cannot find dark color from the drawable darkSwatch will be null
            return;
        }

        int darkerColor = ColorUtils.getDarkerColor(darkSwatch.getRgb());

        RelativeLayout textPanel = (RelativeLayout) getView().findViewById(R.id.textPanel);

        if (textPanel != null) {
            textPanel.setBackgroundColor(darkerColor);
        }
    }

    private void loadImage(ImageView imageView, String imageUrl) {
        Log.i(TAG, "loadImage url = " + imageUrl);
        mPicasso.load(imageUrl)
                .placeholder(R.drawable.tile_img_placeholder)
                .into(imageView);
    }

    private void setFavouriteToggleListener() {
        ToggleButton toggleFavourite = (ToggleButton) getView().findViewById(R.id.toggleFav);

        boolean isFavMovie = mFavouritesDao.isFavouriteMovie(mMoiveId);
        if (isFavMovie) {
            toggleFavourite.setChecked(true);
        }

        toggleFavourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    long id = mFavouritesDao.addFavouriteMovie(mMoiveId, mMovieDetailInfo.getPosterPath());
                    Log. d(TAG, "Successfully favourited movie " + mMoiveId + ". id = " + id);
                } else {
                    int count = mFavouritesDao.removeFavaouriteMovie(mMoiveId);
                    if (count == 1 ) {
                        Log.d(TAG, "Successfully deleted favourite movie");
                    } else {
                        Log.d(TAG, "Error while deleting favourite movie");
                    }
                }

            }
        });
    }

}
