package com.example.lenovo.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

/**
 * Created by Lenovo on 21.11.2017.
 */

public class ScreenSlidePagerActivity extends android.support.v4.app.FragmentActivity{

    private static final int NUM_PAGES = ActivityUserList.users.size();
    private ViewPager mPager;
    private int startPosition;
    private PagerAdapter mPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);
        Intent intent = getIntent();
        startPosition = intent.getIntExtra("index",0);
        mPager = (ViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(startPosition);
    }



    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }


    private class ScreenSlidePagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        private Bundle bundle;
        ScreenSlidePageFragment fragment;
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return ActivityUserList.users.get(position).getName();
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            bundle = new Bundle();
            bundle.putInt("index", position);
            fragment = new ScreenSlidePageFragment();
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }




}
