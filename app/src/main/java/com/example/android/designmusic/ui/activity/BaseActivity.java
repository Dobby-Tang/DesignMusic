package com.example.android.designmusic.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.designmusic.IAudioStatusChangeListener;
import com.example.android.designmusic.ISongManager;
import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.player.Constant;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wnafee.vector.MorphButton;

import java.util.ArrayList;

/**
*@author By Dobby Tang
*Created on 2016-04-18 13:24
*/
public abstract class BaseActivity extends Activity{

    private static final String TAG = "BaseActivity";

    private TextView songName;
    private TextView artistName;
    private MorphButton play;
    private SimpleDraweeView albumCover;
    private LinearLayout playingBottomView;

    private ISongManager mISongManager;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mISongManager = ISongManager.Stub.asInterface(service);
            initISongManager(mISongManager);
            try {
                mISongManager.registerAudioCallBack(mlistener);
                if (mISongManager.getSongPosition() < 0){
                    playingBottomView.setVisibility(View.GONE);
                }else {
                    updateBottomPlayView(mISongManager.getSongItem());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private IAudioStatusChangeListener mlistener = new IAudioStatusChangeListener.Stub() {
        @Override
        public void AudioIsStop() throws RemoteException {
            Message msg = Message.obtain();
            msg.what = Constant.IS_UN_PLAYING;
            mHandler.sendMessage(msg);
        }

        @Override
        public void AudioIsPause() throws RemoteException {
            Message msg = Message.obtain();
            msg.what = Constant.IS_UN_PLAYING;
            mHandler.sendMessage(msg);
        }

        @Override
        public void AudioIsPlaying() throws RemoteException {
            Message msg = Message.obtain();
            msg.what = Constant.IS_PLAYING;
            mHandler.sendMessage(msg);
        }

        @Override
        public void playingCallback(int position,Song song) throws RemoteException {
            Log.d(TAG, "playingCallback: "+ song.song.get(LoadingMusicTask.songName));
            Message msg = Message.obtain();
            msg.what = Constant.PLAYING_CALL_BACK;
            msg.obj = song;
            mHandler.sendMessage(msg);
        }
    };

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handleMessageCallback(msg);
        }
    };


    private void initBottomView(){
        View view = LayoutInflater.from(this).inflate(R.layout.activity_now_playing,null);
        songName = (TextView)view.findViewById(R.id.home_music_name);
        artistName = (TextView)view.findViewById(R.id.home_artist_name);
        play = (MorphButton)view.findViewById(R.id.home_music_playBtn);
        albumCover = (SimpleDraweeView)view.findViewById(R.id.home_music_album_img);
        playingBottomView = (LinearLayout)findViewById(R.id.playing_bottom_view);

        playingBottomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(BaseActivity.this, MusicPlayerActivity.class);
                    intent.putExtra(Constant.PLAYIONG_POSITION,mISongManager.getPlayingListPosition());
                    intent.putExtra(Constant.PLAYIONG_LIST
                            ,(ArrayList<Song>) mISongManager.getSongList());
                    startActivity(intent);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        play.setOnStateChangedListener(new MorphButton.OnStateChangedListener() {
            @Override
            public void onStateChanged(MorphButton.MorphState changedTo, boolean isAnimating) {
                switch (changedTo){
                    case START:
                        try {
                            int position = -1;
                            position = mISongManager.getSongPosition();
                            if (position >= 0){
                                mISongManager.play(position);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    case END:
                        try {
                            mISongManager.pause();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                }

            }
        });
    }

    private void updateBottomPlayView(Song song) throws RemoteException {
        if (song != null){
            int paddingInDP = 64;  // 64 dps
            final float scale = getResources().getDisplayMetrics().density;
            int paddingInPx = (int) (paddingInDP * scale + 0.5f);
//            viewPager.setPadding(0,0,0,padding_in_px);
            playingBottomView.setVisibility(View.VISIBLE);
            songName.setText(song.song.get(LoadingMusicTask.songName));
            artistName.setText(song.song.get(LoadingMusicTask.artistName));
            Uri uri = ContentUris.withAppendedId(LoadingMusicTask.albumArtUri
                    ,Integer.valueOf(song.song.get(LoadingMusicTask.albumId)));
            albumCover.setImageURI(uri);
        }
    }


    abstract protected void handleMessageCallback(Message msg);
    abstract protected void initISongManager(ISongManager mISongManager);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        initBottomView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mISongManager.unregisterAudioCallBack(mlistener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
