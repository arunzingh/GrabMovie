package com.grabtaxi.grabmovie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.grabtaxi.grabmovie.R;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TilesListAdapter extends BaseAdapter {

    private List<Item> mItems;
    private Collection<View> mTileRecycleBin;
    private final LayoutInflater mLayoutInflater;
    private int mPadding;
    private int mTopRowPadding;

    public TilesListAdapter(Context context, LayoutInflater layoutInflater) {
        mLayoutInflater = layoutInflater;
        mTileRecycleBin = Lists.newArrayList();
        mPadding = mLayoutInflater.getContext().getResources().getDimensionPixelSize(R.dimen.tile_margin);
        mTopRowPadding = mLayoutInflater.getContext().getResources().getDimensionPixelSize(R.dimen.top_row_padding);
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mItems.get(position).getView(mTileRecycleBin, mLayoutInflater, convertView, parent);
        if (position == 0) {
            view.setPadding(mPadding, mTopRowPadding, mPadding, mPadding);
        } else if (position == mItems.size() - 1) {
            view.setPadding(mPadding, mPadding, mPadding, mTopRowPadding);
        } else {
            view.setPadding(mPadding, mPadding, mPadding, mPadding);
        }
        return view;
    }


    public void setData(List<Item> items) {
        mItems = items;
        notifyDataSetChanged();
    }


    /**
     * Represents a generic item this adapter can hold.
     */
    public interface Item {
        public View getView(Collection<View> tileRecycleBin,
                            LayoutInflater inflater, View convertView, ViewGroup viewGroup);
    }
}
