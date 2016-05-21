package com.grabtaxi.grabmovie.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.astuetz.PagerSlidingTabStrip;
import com.grabtaxi.grabmovie.R;
import com.grabtaxi.grabmovie.fragments.TilesFragment;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    public static final int POS_NOW_SHOWING = 0;
    public static final int POS_FAVOURITES = 1;
    public static final int COUNT_PAGES = 2;

    private static final String TAG_NOW_SHOWING_FRAGMENT = "now_showing";
    private static final String TAG_FAVOURITES_FRAGMENT = "favourites";


    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_home);

        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(mPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Activity destroyed");
    }

    private class HomePagerAdapter extends FragmentStatePagerAdapter {
        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "PagerAdapter:position = " + position);
            TilesFragment fragment = null;
            fragment = TilesFragment.newInstance(position);
            return fragment;
        }

        @Override
        public int getCount() {
            return COUNT_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == POS_NOW_SHOWING)
                return  getString(R.string.now_showing);
            else if (position == POS_FAVOURITES)
                return  getString(R.string.favourites);
            else
                return null;

        }
    }

}
