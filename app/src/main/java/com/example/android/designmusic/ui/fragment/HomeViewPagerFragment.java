package com.example.android.designmusic.ui.fragment;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.designmusic.R;
import com.example.android.designmusic.ui.adapter.HomeFragmentPagerAdapter;

/**
*@author By Dobby Tang
*Created on 2016-04-19 15:15
*/
public class HomeViewPagerFragment extends Fragment {

   private ViewPager viewPager;
    private Resources resources;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_home_viewpager,container,false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        resources = getResources();
        if(viewPager != null){
            setupViewPager(viewPager);
        }

        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager){
        HomeFragmentPagerAdapter homeFragmentPagerAdapter =
                new HomeFragmentPagerAdapter(getFragmentManager());

        SongFragment songFragment = SongFragment.newInstance(SongFragment.TYPE_SONG);
        homeFragmentPagerAdapter.addFragment(
                songFragment,resources.getString(R.string.local_song));

        ArtistFragment artistFragment = ArtistFragment.newInstance(ArtistFragment.TYPE_ARTIST);
        homeFragmentPagerAdapter.addFragment(
                artistFragment,resources.getString(R.string.local_artist));

        AlbumFragment albumFragment = AlbumFragment.newInstance(AlbumFragment.TYPE_ALBUM);
        homeFragmentPagerAdapter.addFragment(
                albumFragment,resources.getString(R.string.local_album));

        viewPager.setAdapter(homeFragmentPagerAdapter);
    }



}
