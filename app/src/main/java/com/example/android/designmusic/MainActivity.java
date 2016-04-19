package com.example.android.designmusic;

import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.android.designmusic.ui.activity.BaseActivity;
import com.example.android.designmusic.ui.fragment.HomeViewPagerFragment;


/**
*@author By Dobby Tang
*Created on 2016-03-04 15:36
*/
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private FrameLayout homeFrameLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private HomeViewPagerFragment homeViewPagerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setBottomViewVisibility(true);
        initBottomView();

        initView();
        initStartFragment();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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


    @Override
    protected void handleMessageCallback(Message msg) {

    }

    @Override
    protected void initISongManager(ISongManager mISongManager) {

    }

    @Override
    protected void bottomIsVisibility(boolean isVisibility, int height) {
        if (isVisibility && homeFrameLayout != null){
            homeFrameLayout.setPadding(0,0,0,height);
        }else{
            homeFrameLayout.setPadding(0,0,0,0);
        }
    }


    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setElevation(0);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.main_nav_view);
        homeFrameLayout = (FrameLayout)findViewById(R.id.home_fragment);
        homeViewPagerFragment = new HomeViewPagerFragment();

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.music_library:
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.home_fragment, homeViewPagerFragment).commit();
                                item.setChecked(true);
                                toolbar.setTitle(item.getTitle());
                                break;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                }

        );
    }

    private void initStartFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_fragment, homeViewPagerFragment).commit();
        navigationView.getMenu().getItem(1).setChecked(true);
    }
}
