package com.example.android.designmusic.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;

import com.example.android.designmusic.R;
import com.example.android.designmusic.ui.fragment_v21.MusicPlayerFragmentV21;

/**
 * Created by KJHmobileAdmin on 2016-03-15.
 */
public class MusicPlayerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            initMusicPlayerFragmentV21();
        }else{
            initMusicPlayerFragment();
        }
    }


    private void initMusicPlayerFragmentV21() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.music_player_layout, MusicPlayerFragmentV21.newInstance())
                .commit();
    }

    private void initMusicPlayerFragment(){

    }
}
