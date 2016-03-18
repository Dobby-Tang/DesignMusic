package com.example.android.designmusic.ui.fragment;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    private MorphButton playerBtn;
    private SimpleDraweeView musicCover;
    private ImageView nextBtn;

    int position;
    private static ArrayList<Song> mPlayingList;

    MorphButton.MorphState START = MorphButton.MorphState.START;
    MorphButton.MorphState END = MorphButton.MorphState.END;

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




    private IAudioStatusChangeListener mListener = new IAudioStatusChangeListener.Stub() {
        @Override
        public void AudioIsStop() throws RemoteException {
            getActivity().finish();
        }
    };


    @Override
    public void onResume() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unbindService(songPlayerServiceConnection);
    }

}
