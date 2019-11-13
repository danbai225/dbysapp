package com.danbai.dbysapp.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class FenFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] mTitles = new String[]{"电影", "电视剧", "综艺","动漫","直播"};
    public FenFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return TypeFragment.newTypeFragment(mTitles[position]);
    }
    @Override
    public int getCount() {
        return mTitles.length;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
