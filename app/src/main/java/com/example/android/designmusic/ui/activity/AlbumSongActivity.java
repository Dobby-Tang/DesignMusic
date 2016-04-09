package com.example.android.designmusic.ui.activity;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.designmusic.IAudioStatusChangeListener;
import com.example.android.designmusic.ISongManager;
import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.player.service.MusicService;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.example.android.designmusic.ui.adapter.BaseListAdapter;
import com.example.android.designmusic.ui.adapter.ArtistSongListAdapter;
import com.example.android.designmusic.ui.fragment.HomeFragment;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class AlbumSongActivity extends AppCompatActivity {

    private static final String TAG = "AlbumSongActivity";

    private static final int IS_PLAYING = 0;
    private static final int IS_UN_PLAYING = 1;
    private static final int ALBUM_SONG_LIST = 5;


    private String albumName;
    private String albumId;
    private String artistName;

    private ArrayList<Song> SongList;

    private RecyclerView mDetailList;
    private SimpleDraweeView albumCover;
    private TextView artistNameTextView;
    private TextView songNumberTextView;
    private FloatingActionButton fab;

    private ArtistSongListAdapter mDetailSongListAdapter;
    private ISongManager mISongManager;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mISongManager = ISongManager.Stub.asInterface(service);
            try {
                mISongManager.registerAudioCallBack(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mISongManager = null;
        }
    };


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ALBUM_SONG_LIST:
                    ArrayList<Song> albumSongList = (ArrayList<Song>) msg.obj;
                    songNumberTextView.setText("曲目: " + msg.arg1);
                    if (mDetailSongListAdapter != null){
                        mDetailSongListAdapter.setDatas(albumSongList);
                        mDetailSongListAdapter.notifyDataSetChanged();
                    }
                    if (mISongManager != null && mDetailSongListAdapter.getData() != null){
                        try {
                            ArrayList<Song> mSongList = mDetailSongListAdapter.getData();
                            if (mISongManager.isEqualsSongList(mSongList)){
                                Log.d(TAG, "mServiceConnection: now playing is mSongList");
                                if (mISongManager.isPlaying()){
                                    Log.d(TAG, "mServiceConnection: playing is true");
                                    fab.setImageResource(R.mipmap.pause);
                                }else{
                                    Log.d(TAG, "mServiceConnection: playing is false");
                                    fab.setImageResource(R.mipmap.play);
                                }
                            }else {
                                Log.d(TAG, "mServiceConnection: now playing list is not mSongList");
                                fab.setImageResource(R.mipmap.play);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case IS_PLAYING:
                    fab.setImageResource(R.mipmap.pause);
                    break;
                case IS_UN_PLAYING:
                    fab.setImageResource(R.mipmap.play);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_song);

        Intent intent = new Intent(AlbumSongActivity.this, MusicService.class);
        bindService(intent,mServiceConnection, Context.BIND_AUTO_CREATE);

        albumCover = (SimpleDraweeView)findViewById(R.id.album_cover);
        mDetailList = (RecyclerView)findViewById(R.id.detail_list);
        artistNameTextView = (TextView)findViewById(R.id.detail_artist_name);
        songNumberTextView = (TextView)findViewById(R.id.detail_song_num);

        SongList = getIntent().getParcelableArrayListExtra(HomeFragment.SONG_LIST);
        albumName = getIntent().getStringExtra(LoadingMusicTask.albumName);
        albumId = getIntent().getStringExtra(LoadingMusicTask.albumId);
        artistName = getIntent().getStringExtra(LoadingMusicTask.artistName);


        artistNameTextView.setText(artistName);
        Uri uri = ContentUris.withAppendedId(LoadingMusicTask.albumArtUri
                ,Integer.valueOf(albumId));
        albumCover.setImageURI(uri);

        mDetailSongListAdapter = new ArtistSongListAdapter();
        mDetailSongListAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                Intent intent = new Intent(AlbumSongActivity.this, MusicPlayerActivity.class);
                intent.putExtra(HomeFragment.PLAYIONG_POSITION,position);
                intent.putExtra(HomeFragment.PLAYIONG_LIST
                        ,(ArrayList<Song>) mDetailSongListAdapter.getData());
                startActivity(intent);
            }
        });
        mDetailList.setLayoutManager(new LinearLayoutManager(this));
        mDetailList.setAdapter(mDetailSongListAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                initAlbumSongList(SongList,albumId);
            }
        }).start();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(albumName);

        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mISongManager != null && mDetailSongListAdapter.getData() != null){
                    try {
                        if (mISongManager.isPlaying()) {
                            if (mISongManager.isEqualsSongList(
                                    mDetailSongListAdapter.getData())){
                                fab.setImageResource(R.mipmap.play);
                                mISongManager.pause();
                            }else {
                                fab.setImageResource(R.mipmap.pause);
                                mISongManager.initSongList(mDetailSongListAdapter.getData());
                                mISongManager.setPlayingMode(MusicPlayerActivity.PLAYING_REPEAT);
                                if (mISongManager.getSongItem() != -1){
                                    mISongManager.play(mISongManager.getSongItem(),false);
                                }else {
                                    mISongManager.play(0,false);
                                }
                            }
                        } else {
                            fab.setImageResource(R.mipmap.pause);
                            mISongManager.initSongList(mDetailSongListAdapter.getData());
                            mISongManager.setPlayingMode(MusicPlayerActivity.PLAYING_REPEAT);
                            if (mISongManager.getSongItem() != -1){
                                mISongManager.play(mISongManager.getSongItem(),false);
                            }else {
                                mISongManager.play(0,false);
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
            if (mISongManager != null){
                if (mISongManager.isPlaying()){
                    fab.setImageResource(R.mipmap.pause);
                }else {
                    fab.setImageResource(R.mipmap.play);
                }
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mISongManager.registerAudioCallBack(mListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(mServiceConnection);
    }

    IAudioStatusChangeListener mListener = new IAudioStatusChangeListener.Stub() {
        @Override
        public void AudioIsStop() throws RemoteException {
            Message msg = Message.obtain();
            msg.what = IS_UN_PLAYING;
            mHandler.sendMessage(msg);
        }

        @Override
        public void AudioIsPause() throws RemoteException {
            Message msg = Message.obtain();
            msg.what = IS_UN_PLAYING;
            mHandler.sendMessage(msg);
        }

        @Override
        public void AudioIsPlaying() throws RemoteException {
            Message msg = Message.obtain();
            msg.what = IS_PLAYING;
            mHandler.sendMessage(msg);
        }

        @Override
        public void playingCallback(int position) throws RemoteException {
            if (mISongManager.isPlaying()){
                Message msg = Message.obtain();
                msg.what = IS_PLAYING;
                mHandler.sendMessage(msg);
            }else{
                Message msg = Message.obtain();
                msg.what = IS_UN_PLAYING;
                mHandler.sendMessage(msg);
            }
        }

    };

    public void initAlbumSongList(ArrayList<Song> songList, String albumId){
        ArrayList<Song> albumSongList = new ArrayList<>();
        int songNumber = 0;
        if (songList != null){
            for (int i = 0; i< songList.size();i++){
                if (songList.get(i).song.get(LoadingMusicTask.albumId).equals(albumId)){
                    albumSongList.add(songList.get(i));
                    songNumber++;
                }
            }
        }

        Message msg = Message.obtain();
        msg.what = ALBUM_SONG_LIST;
        msg.arg1 = songNumber;
        msg.obj = albumSongList;
        mHandler.sendMessage(msg);

    }



}
