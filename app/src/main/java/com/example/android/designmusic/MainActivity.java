package com.example.android.designmusic;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.designmusic.ui.activity.BaseActivity;
import com.example.android.designmusic.ui.adapter.HomeFragmentPagerAdapter;
import com.example.android.designmusic.ui.fragment.AlbumFragment;
import com.example.android.designmusic.ui.fragment.ArtistFragment;
import com.example.android.designmusic.ui.fragment.SongFragment;


/**
*@author By Dobby Tang
*Created on 2016-03-04 15:36
*/
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private Resources resources;

    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setBottomViewVisibility(true);
        initBottomView();

        resources = getResources();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.main_nav_view);
        if(navigationView != null){
            setupDrawerContent(navigationView);
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if(viewPager != null){
            setupViewPager(viewPager);
        }

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

//        Intent intent = new Intent(this, MusicService.class);
//        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);

//        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager){
        HomeFragmentPagerAdapter homeFragmentPagerAdapter =
                new HomeFragmentPagerAdapter(getSupportFragmentManager());

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


    @Override
    protected void handleMessageCallback(Message msg) {

    }

    @Override
    protected void initISongManager(ISongManager mISongManager) {

    }

    @Override
    protected void bottomIsVisibility(boolean isVisibility, int height) {
        if (isVisibility){
            viewPager.setPadding(0,0,0,height);
        }else{
            viewPager.setPadding(0,0,0,0);
        }
    }


    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        if(item.getItemId() != R.id.sub_1 && item.getItemId() != R.id.sub_2){
                        item.setChecked(true);
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                }

        );
    }
}
