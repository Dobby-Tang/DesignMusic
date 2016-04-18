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
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.designmusic.IAudioStatusChangeListener;
import com.example.android.designmusic.ISongManager;
import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.player.Constant;
import com.example.android.designmusic.player.service.MusicService;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wnafee.vector.MorphButton;

import java.util.ArrayList;

/**
*@author By Dobby Tang
*Created on 2016-04-18 13:24
*/
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    private static final boolean BOOTOM_VISIBILITY = true;
    private static final boolean BOOTOM_GONE = false;

    private boolean bottomViewIsVisibility = false;

    private TextView songName;
    private TextView artistName;
    private MorphButton play;
    private SimpleDraweeView albumCover;

    private View playingBottomView;
    private CoordinatorLayout bottomViewGroup;
    private int bottomViewHeight;

    /**
    *为true时显示bottomView，反之不显示
    */
    protected void setBottomViewVisibility(boolean visibility){
        bottomViewIsVisibility = visibility;
    }

    private ISongManager mISongManager;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mISongManager = ISongManager.Stub.asInterface(service);
            initISongManager(mISongManager);
            try {
                mISongManager.registerAudioCallBack(mlistener);
                if (bottomViewIsVisibility){
                    if (mISongManager.isPlaying()) {
                        play.setState(MorphButton.MorphState.START);
                    } else {
                        play.setState(MorphButton.MorphState.END);
                    }
                }
                updateBottomPlayView(mISongManager.getSongItem());
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
            switch (msg.what){
                case Constant.PLAYING_CALL_BACK:
                    Song song = (Song)msg.obj;
                    try {
                        updateBottomPlayView(song);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case Constant.IS_PLAYING:
                    play.setState(MorphButton.MorphState.START);
                    break;
                case Constant.IS_UN_PLAYING:
                    play.setState(MorphButton.MorphState.END);
                    break;
            }
            handleMessageCallback(msg);
        }
    };


    public void initBottomView(){

        if (bottomViewIsVisibility){
            bottomViewGroup= (CoordinatorLayout) ((ViewGroup) this
                    .findViewById(android.R.id.content)).getChildAt(0);

            playingBottomView = LayoutInflater.from(this).inflate(R.layout.activity_now_playing,null);
            songName = (TextView)playingBottomView.findViewById(R.id.home_music_name);
            artistName = (TextView)playingBottomView.findViewById(R.id.home_artist_name);
            play = (MorphButton)playingBottomView.findViewById(R.id.home_music_playBtn);
            albumCover = (SimpleDraweeView)playingBottomView.findViewById(R.id.home_music_album_img);
//        playingBottomView = (LinearLayout)findViewById(R.id.playing_bottom_view);

            int paddingInDP = 64;  // 64 dps
            final float scale = getResources().getDisplayMetrics().density;
            bottomViewHeight = (int) (paddingInDP * scale + 0.5f);

            CoordinatorLayout.LayoutParams lp = new CoordinatorLayout
                    .LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                    CoordinatorLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.BOTTOM;
            bottomViewGroup.addView(playingBottomView,lp);

            playingBottomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(BaseActivity.this, MusicPlayerActivity.class);
                        intent.putExtra(Constant.PLAYING_POSITION,mISongManager.getPlayingListPosition());
                        intent.putExtra(Constant.PLAYING_LIST
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

    }


    private void updateBottomPlayView(Song song) throws RemoteException {
        if (bottomViewIsVisibility){
            if (song != null){
                bottomIsVisibility(BOOTOM_VISIBILITY,bottomViewHeight);
                playingBottomView.setVisibility(View.VISIBLE);
                songName.setText(song.song.get(LoadingMusicTask.songName));
                artistName.setText(song.song.get(LoadingMusicTask.artistName));
                Uri uri = ContentUris.withAppendedId(LoadingMusicTask.albumArtUri
                        ,Integer.valueOf(song.song.get(LoadingMusicTask.albumId)));
                albumCover.setImageURI(uri);
            }else {
                Log.d(TAG, "updateBottomPlayView: set bottom is gone");
                bottomIsVisibility(BOOTOM_GONE,bottomViewHeight);
                playingBottomView.setVisibility(View.GONE);
            }
        }
    }


    abstract protected void handleMessageCallback(Message msg);
    abstract protected void initISongManager(ISongManager mISongManager);
    abstract protected void bottomIsVisibility(boolean isVisibility,int height);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (bottomViewIsVisibility){
            if (mISongManager != null){
                try {
                    if (mISongManager.isPlaying()){
                        play.setState(MorphButton.MorphState.START);
                    }else {
                        play.setState(MorphButton.MorphState.END);
                    }
                    updateBottomPlayView(mISongManager.getSongItem());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }else{
                Log.d(TAG, "onStart: mISongManager is null");
                bottomIsVisibility(BOOTOM_GONE,bottomViewHeight);
                playingBottomView.setVisibility(View.GONE);
            }
        }
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
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mISongManager != null){
            try {
                mISongManager.unregisterAudioCallBack(mlistener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(serviceConnection);
    }
}
