package com.example.android.designmusic;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.designmusic.player.service.MusicService;
import com.example.android.designmusic.ui.adapter.HomeFragmentPagerAdapter;
import com.example.android.designmusic.ui.fragment.AlbumFragment;
import com.example.android.designmusic.ui.fragment.ArtistFragment;
import com.example.android.designmusic.ui.fragment.SongFragment;
import com.facebook.drawee.backends.pipeline.Fresco;


/**
*@author By Dobby Tang
*Created on 2016-03-04 15:36
*/
public class MainActivity extends AppCompatActivity {

    public static final String PLAYIONG_LIST = "Playing_list";             //所有本地歌曲
    public static final String PLAYIONG_POSITION = "Playing_position";     //播放歌曲序号
    public static final String SONG_LIST = "album_song_list" ;        //专辑歌曲列表

    private DrawerLayout mDrawerLayout;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(MainActivity.this);
        setContentView(R.layout.activity_main);

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

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
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

        Intent intent = new Intent(MainActivity.this, MusicService.class);
        startService(intent);
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
