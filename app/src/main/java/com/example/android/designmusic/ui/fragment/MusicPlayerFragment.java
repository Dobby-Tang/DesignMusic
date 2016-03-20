package com.example.android.designmusic.ui.fragment;

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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.designmusic.IAudioStatusChangeListener;
import com.example.android.designmusic.ISongManager;
import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.player.service.MusicService;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wnafee.vector.MorphButton;

import java.util.ArrayList;

/**
*@author By Dobby Tang
*Created on 2016-03-15 13:51
*/

public class MusicPlayerFragment extends Fragment{
    private static final String TAG = "MusicPlayerFragment";
    private static final int playingCallBack = 1;
    private static final int pauseCallBack = 0;

    private static MorphButton playerBtn;
    private SimpleDraweeView musicCover;
    private ImageView nextBtn;

    int position;
    private static ArrayList<Song> mPlayingList;

    static MorphButton.MorphState START = MorphButton.MorphState.START;
    static MorphButton.MorphState END = MorphButton.MorphState.END;

    public ISongManager mISongManager = null;

    private ServiceConnection songPlayerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mISongManager = ISongManager.Stub.asInterface(service);
            try {
                mISongManager.registerCallBack(mListener);
                mISongManager.initSongList(mPlayingList);
                mISongManager.play(position);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mISongManager = null;
        }
    };

    private static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == playingCallBack){
                playerBtn.setState(START);
                Log.d(TAG,"setState START");
            }else if (msg.what == pauseCallBack){
                playerBtn.setState(END);
                Log.d(TAG,"setState END");
            }
        }
    };


    public static MusicPlayerFragment newInstance(ArrayList<Song> mPlayingList, int position) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(HomeFragment.PLAYIONG_LIST,mPlayingList);
        args.putInt(HomeFragment.PLAYIONG_POSITION,position);
        MusicPlayerFragment fragment = new MusicPlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mPlayingList = getArguments().getParcelableArrayList(HomeFragment.PLAYIONG_LIST);
            position = getArguments().getInt(HomeFragment.PLAYIONG_POSITION,0);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_player,container,false);
        Song mSong = mPlayingList.get(position);

        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent,songPlayerServiceConnection, Context.BIND_AUTO_CREATE);

        musicCover = (SimpleDraweeView) view.findViewById(R.id.music_player_cover);
        playerBtn = (MorphButton) view.findViewById(R.id.music_player_playBtn);
        nextBtn = (ImageView) view .findViewById(R.id.music_player_nextBtn);

        int albumId = Integer.parseInt(mSong.song.get(LoadingMusicTask.albumId));
        Uri uri = ContentUris.withAppendedId(LoadingMusicTask.albumArtUri,albumId);
        musicCover.setImageURI(uri);



//        MorphButton.MorphState mState = playerBtn.getState();
//        //判断playerBtn的状态
//        switch (mState){
//            case START:
//                break;
//            case END:
//                break;
//        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
            boolean b = true;
            @Override
            public void onClick(View v) {
                if(!b){
                    playerBtn.setState(START);
                    b = true;
                }

                if (b){
                    playerBtn.setState(END);
                    b = false;
                }

            }
        });

        playerBtn.setOnStateChangedListener(new MorphButton.OnStateChangedListener() {
            @Override
            public void onStateChanged(MorphButton.MorphState changedTo, boolean isAnimating) {
                switch (changedTo){
                    case START:
                        try {
                            mISongManager.play(position);
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


        return view;
    }



    /**
    *service回调监听器
    *@author Dobby-Tang
    *created at 16/3/20  下午12:31
    */
    private IAudioStatusChangeListener mListener = new IAudioStatusChangeListener.Stub() {
        @Override
        public void AudioIsStop() throws RemoteException {
            Log.d(TAG,"Audio is stop");
            getActivity().finish();
        }

        @Override
        public void AudioIsPause() throws RemoteException {
            Log.d(TAG,"Audio is pause");
            Message msg = Message.obtain();
            msg.what = pauseCallBack;
            mHandler.sendMessage(msg);
        }

        @Override
        public void AudioIsPlaying() throws RemoteException {
            Log.d(TAG,"Audio is playing");
            Message msg = Message.obtain();
            msg.what = playingCallBack;
            mHandler.sendMessage(msg);
        }

    };


    @Override
    public void onStart() {
        super.onStart();
        if (mISongManager != null){
            try {
                if (mISongManager.isPlaying()){
                    playerBtn.setState(START);
                }else {
                    playerBtn.setState(END);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mISongManager.unregisterCallBack(mListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        getActivity().unbindService(songPlayerServiceConnection);
    }

}
