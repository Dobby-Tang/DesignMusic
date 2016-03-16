package com.example.android.designmusic.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.ui.fragment.HomeFragment;
import com.example.android.designmusic.ui.fragment.MusicPlayerFragment;
import com.example.android.designmusic.ui.fragment_v21.MusicPlayerFragmentV21;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;

/**
*@author By Dobby Tang
*Created on 2016-03-15 17:00
*/
public class MusicPlayerActivity extends AppCompatActivity {

    private ArrayList<Song> mplayerList;
    private int playerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(MusicPlayerActivity.this);
        setContentView(R.layout.activity_music_player);

        Intent intent = getIntent();
        mplayerList = intent.getParcelableArrayListExtra(HomeFragment.PLAYIONG_LIST);
        playerPosition = intent.getIntExtra(HomeFragment.PLAYIONG_POSITION,-1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.music_player_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            initMusicPlayerFragmentV21();
        }else{
            initMusicPlayerFragment();
        }
    }


    private void initMusicPlayerFragmentV21() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.music_player_layout, MusicPlayerFragmentV21.newInstance())
                .commit();
    }

    private void initMusicPlayerFragment(){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.music_player_layout, MusicPlayerFragment.newInstance(mplayerList,playerPosition))
                .commit();
    }
}
