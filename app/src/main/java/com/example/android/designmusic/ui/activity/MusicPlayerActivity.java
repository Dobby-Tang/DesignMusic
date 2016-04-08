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
import com.example.android.designmusic.task.LoadingMusicTask;
import com.example.android.designmusic.ui.adapter.BaseListAdapter;
import com.example.android.designmusic.ui.adapter.PlayingListAdapter;
import com.example.android.designmusic.ui.fragment.HomeFragment;
import com.example.android.designmusic.ui.fragment.MusicPlayerFragment;
import com.example.android.designmusic.ui.fragment_v21.MusicPlayerFragmentV21;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;

/**
*@author By Dobby Tang
*Created on 2016-03-15 17:00
*/
public class MusicPlayerActivity extends AppCompatActivity implements MusicPlayerFragment.playingCallback {

    private static final String TAG = "MusicPlayerActivity";

    public static final int PLAYING_REPEAT = 1;               //列表循环
    public static final int PLAYING_REPEAT_ONE = 2;           //单曲循环
    public static final int PLAYING_RANDOM  = 3;              //随机播放

    private int playingMode = -1;

    private ArrayList<Song> mplayerList;
    private int playerPosition;

    private ISongManager mISongManager;
    private FloatingActionButton fab;

    private CoordinatorLayout mCoordinatorLayout;

    private RecyclerView bottomListView;
    private TextView songNum;
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
                            case PLAYING_REPEAT:
                                mISongManager.setPlayingMode(PLAYING_REPEAT_ONE);
                                break;
                            case PLAYING_REPEAT_ONE:
                                mISongManager.setPlayingMode(PLAYING_RANDOM);
                                break;
                            case PLAYING_RANDOM:
                                mISongManager.setPlayingMode(PLAYING_REPEAT);
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
        mplayerList = intent.getParcelableArrayListExtra(HomeFragment.PLAYIONG_LIST);
        playerPosition = intent.getIntExtra(HomeFragment.PLAYIONG_POSITION,-1);

        songNum = (TextView)findViewById(R.id.music_player_playing_num);
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
                    mISongManager.play(position,true);
                    bottomSheetDialog.dismiss();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void initMusicPlayerFragmentV21() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.music_player_layout, MusicPlayerFragmentV21.newInstance())
                .commit();
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
                case PLAYING_REPEAT:
                    fab.setImageResource(R.mipmap.music_player_repeat);
                    break;
                case PLAYING_REPEAT_ONE:
                    fab.setImageResource(R.mipmap.music_player_repeat_one);
                    break;
                case PLAYING_RANDOM:
                    fab.setImageResource(R.mipmap.music_player_arrow_shuffle);
                    break;
            }
            mISongManager.setPlayingMode(playingMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onSongPosition(int position) {
        if (position >= 0 ){
            playerPosition = position;
            songNum.setText(position + 1 + "");
            songName.setText(mplayerList.get(position).song.get(LoadingMusicTask.songName));
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
