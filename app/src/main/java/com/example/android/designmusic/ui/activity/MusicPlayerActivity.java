package com.example.android.designmusic.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.designmusic.ISongManager;
import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.player.Constant;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.example.android.designmusic.ui.adapter.BaseListAdapter;
import com.example.android.designmusic.ui.adapter.PlayingListAdapter;
import com.example.android.designmusic.ui.fragment.MusicPlayerFragment;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;

/**
*@author By Dobby Tang
*Created on 2016-03-15 17:00
*/
public class MusicPlayerActivity extends AppCompatActivity implements MusicPlayerFragment.playingCallback {

    private static final String TAG = "MusicPlayerActivity";

    private int playingMode = Constant.PLAYING_REPEAT;

    private ArrayList<Song> mplayerList;
    private int playerPosition;

    private ISongManager mISongManager;
    private FloatingActionButton fab;

    private CoordinatorLayout mCoordinatorLayout;

    private RecyclerView bottomListView;
    private TextView artistName;
    private TextView songName;
    private ImageView songListBtn;
    private BottomSheetDialog bottomSheetDialog;

    private PlayingListAdapter playingListAdapter;

    private MusicPlayerFragment musicPlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(MusicPlayerActivity.this);
        setContentView(R.layout.activity_music_player);

        Toolbar toolbar = (Toolbar) findViewById(R.id.music_player_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        fab = (FloatingActionButton)
                findViewById(R.id.show_playing_queue_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mISongManager != null){
                    try{
                        switch (playingMode){
                            case Constant.PLAYING_REPEAT:
                                mISongManager.setPlayingMode(Constant.PLAYING_REPEAT_ONE);
                                break;
                            case Constant.PLAYING_REPEAT_ONE:
                                mISongManager.setPlayingMode(Constant.PLAYING_RANDOM);
                                break;
                            case Constant.PLAYING_RANDOM:
                                mISongManager.setPlayingMode(Constant.PLAYING_REPEAT);
                                break;
                        }
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                    setFabSrc();
                }
            }
        });


        initfindView();
        initMusicPlayerFragment();
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            initMusicPlayerFragmentV21();
//        }else{
//
//        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case android.R.id.home:
               finish();
               return true;
       }
        return super.onOptionsItemSelected(item);
    }

    private void initfindView(){

        Intent intent = getIntent();
        mplayerList = intent.getParcelableArrayListExtra(Constant.PLAYING_LIST);
        playerPosition = intent.getIntExtra(Constant.PLAYING_POSITION,-1);

        artistName = (TextView)findViewById(R.id.music_player_playing_artistname);
        songName = (TextView)findViewById(R.id.music_player_playing_songname);
        songListBtn = (ImageView)findViewById(R.id.music_player_song_list_btn);

        songListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        mCoordinatorLayout = (CoordinatorLayout) LayoutInflater.from(this)
                .inflate(R.layout.activity_music_player,null);

        playingListAdapter = new PlayingListAdapter();
        playingListAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                try {
                    mISongManager.play(position);
                    bottomSheetDialog.dismiss();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    private void initMusicPlayerFragment(){
        musicPlayerFragment = new MusicPlayerFragment();
//        musicPlayerFragment.newInstance(mplayerList,playerPosition);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.music_player_layout, musicPlayerFragment
                        .newInstance(mplayerList,playerPosition)).commit();
    }

    private void showBottomSheetDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.playing_bottom_dialog
                ,null);
        bottomListView = (RecyclerView)view.findViewById(R.id.music_player_playing_list);
        bottomListView.setLayoutManager(new LinearLayoutManager(this));

        bottomListView.setAdapter(playingListAdapter);
        bottomListView.scrollToPosition(playerPosition);
        bottomSheetDialog = new BottomSheetDialog(MusicPlayerActivity.this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }


    private void setFabSrc(){
        try {
            playingMode = mISongManager.getPlayingMode();
            switch (playingMode){
                case Constant.PLAYING_REPEAT:
                    fab.setImageResource(R.mipmap.music_player_repeat);
                    break;
                case Constant.PLAYING_REPEAT_ONE:
                    fab.setImageResource(R.mipmap.music_player_repeat_one);
                    break;
                case Constant.PLAYING_RANDOM:
                    fab.setImageResource(R.mipmap.music_player_arrow_shuffle);
                    break;
            }
            mISongManager.setPlayingMode(playingMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onSongPosition(Song song,int position) {
        playerPosition = position;
        if (song != null){
            artistName.setText(song.song.get(LoadingMusicTask.artistName));
            songName.setText(song.song.get(LoadingMusicTask.songName));
        }
    }

    @Override
    public void getSongList(ArrayList<Song> SongList) {
        Log.d(TAG,SongList.getClass().getName());
        if (playingListAdapter != null){
            playingListAdapter.setDatas(SongList);
        }
    }

    @Override
    public void getISongManager(ISongManager mISongManager) {
        this.mISongManager = mISongManager;
        setFabSrc();
    }


}
