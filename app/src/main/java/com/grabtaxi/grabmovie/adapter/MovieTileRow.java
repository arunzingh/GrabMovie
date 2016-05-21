package com.grabtaxi.grabmovie.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.grabtaxi.grabmovie.R;
import com.grabtaxi.grabmovie.activities.MovieDetailActivity;
import com.grabtaxi.grabmovie.fragments.MovieDetailFragment;
import com.grabtaxi.grabmovie.model.MovieInfo;
import com.squareup.picasso.Picasso;

import java.util.Collection;
import java.util.List;

public class MovieTileRow implements TilesListAdapter.Item {

    private static final String TAG = MovieTileRow.class.getSimpleName();

    public static final int TILE_WEIGHT = 1;
    private final int ROW_TILE_SIZE = 3;
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";

    private final List<MovieInfo> mMovieTiles;
    private final Picasso mPicasso;
    private Fragment tilesFragment;

    public MovieTileRow(List<MovieInfo> tiles, Picasso picasso,
                        Fragment frag) {
        mMovieTiles = tiles;
        mPicasso = picasso;
        tilesFragment = frag;
    }

    @Override
    public View getView(Collection<View> tileRecycleBin, LayoutInflater inflater, View convertView, ViewGroup viewGroup) {
        LinearLayout listRow;
        Log.i(TAG, "getView: convertView = " + convertView);
        if (convertView == null) {
            listRow = (LinearLayout) inflater.inflate(R.layout.list_row, viewGroup, false);
        } else {
            listRow = (LinearLayout) convertView;
            // put all the children in the recycle bin
            int childCount = listRow.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = listRow.getChildAt(i);
                view.setOnClickListener(null);
                TileViewHolder holder = (TileViewHolder) view.getTag();
                Log.d(TAG, "holder = " + holder);
                if (holder != null) {
                    resetTileUiState(holder);
                    tileRecycleBin.add(view);
                }
            }
            listRow.removeAllViews();
        }

        int tileSizeSum = 0;

        for (MovieInfo currTile : mMovieTiles) {

            View tileView;
            if (tileRecycleBin.size() > 0) {
                tileView = tileRecycleBin.iterator().next();
                tileRecycleBin.remove(tileView);
            } else {
                tileView = inflater.inflate(R.layout.item_tile, listRow, false);
                TileViewHolder holder = new TileViewHolder(tileView);
                tileView.setTag(holder);
            }

            setTileImageInView(currTile, tileView);
            setOnClickAction(currTile, tileView);

            // We use the weight attribute to span the tile
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tileView.getLayoutParams();
            params.weight = TILE_WEIGHT;
            tileSizeSum += TILE_WEIGHT;

            listRow.addView(tileView, params);
        }

        if (tileSizeSum < ROW_TILE_SIZE) {
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = ROW_TILE_SIZE - tileSizeSum;
            listRow.addView(new View(listRow.getContext()), params);
        }

        return listRow;
    }

    private void setOnClickAction(final MovieInfo currTile, View tileView) {
        tileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onclick " + v);
                Intent launchDetail = new Intent(tilesFragment.getContext(), MovieDetailActivity.class);
                launchDetail.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, currTile.getId());
                tilesFragment.getContext().startActivity(launchDetail);
            }
        });
    }

    private void setTileImageInView(MovieInfo tile, View tileView) {
        TileViewHolder holder = (TileViewHolder) tileView.getTag();
        ImageView imageView = holder.imageView;
        if (imageView != null) {
            // cancel any existing request using this imageView, and clear the imageView
            mPicasso.cancelRequest(holder.imageView);
            imageView.setImageDrawable(null);
            if (tile.getPosterPath() != null) {
                loadImage(holder, IMAGE_BASE_URL + tile.getPosterPath());
            } else {
                imageView.setBackgroundResource(R.drawable.tile_img_placeholder);
            }
        }
    }

    private void loadImage(final TileViewHolder holder, final String imageURL) {
        Log.d(TAG, "loadImage:imageURL = " + imageURL);
        mPicasso.load(imageURL)
                .placeholder(R.drawable.tile_img_placeholder)
                .into(holder.imageView);
    }

    public void prefetchTileImages() {
        for (MovieInfo m : mMovieTiles) {
            String imageUrl = IMAGE_BASE_URL + m.getPosterPath();
            Log.i(TAG, "Prefetching Image = " + imageUrl);
            mPicasso.load(imageUrl).fetch();
        }
    }

    private void resetTileUiState(TileViewHolder holder) {
        if (holder.imageView != null) {
            mPicasso.cancelRequest(holder.imageView);
            holder.imageView.setImageDrawable(null);
        }
    }

    public static class TileViewHolder {
        public final ImageView imageView;

        public TileViewHolder(View tileView) {
            imageView = (ImageView) tileView.findViewById(R.id.imageView);

        }
    }
}
