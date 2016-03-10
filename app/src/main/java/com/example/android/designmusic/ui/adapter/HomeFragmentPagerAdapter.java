package com.example.android.designmusic.ui.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
*@author By Dobby Tang
*Created on 2016-03-10 16:51
*/
public class HomeFragmentPagerAdapter extends FragmentPagerAdapter{
    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitle = new ArrayList<>();

    public HomeFragmentPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    public void addFragment(Fragment mFragment,String title){
        mFragments.add(mFragment);
        mFragmentTitle.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitle.get(position);
    }
}
