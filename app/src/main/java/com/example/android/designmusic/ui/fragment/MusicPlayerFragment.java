package com.example.android.designmusic.ui.fragment;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.player.SongPlayerServiceConnection;
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

    private SongPlayerServiceConnection songPlayerServiceConnection = null;

    private int position;
    private static ArrayList<Song> mPlayingList;

    MorphButton.MorphState START = MorphButton.MorphState.START;
    MorphButton.MorphState END = MorphButton.MorphState.END;

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
        songPlayerServiceConnection = new SongPlayerServiceConnection();
        try {
            songPlayerServiceConnection.initSongList(mPlayingList,position);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent,songPlayerServiceConnection, Context.BIND_AUTO_CREATE);

        final int mPosition = position;


        musicCover = (SimpleDraweeView) view.findViewById(R.id.music_player_cover);
        playerBtn = (MorphButton) view.findViewById(R.id.music_player_playBtn);

        int albumId = Integer.parseInt(mSong.song.get(LoadingMusicTask.albumId));
        Uri uri = ContentUris.withAppendedId(LoadingMusicTask.albumArtUri,albumId);
        musicCover.setImageURI(uri);


        MorphButton.MorphState mState = playerBtn.getState();
        //判断playerBtn的状态
        switch (mState){
            case START:
                break;
            case END:
                break;
        }

        playerBtn.setOnStateChangedListener(new MorphButton.OnStateChangedListener() {
            @Override
            public void onStateChanged(MorphButton.MorphState changedTo, boolean isAnimating) {
                switch (changedTo){
                    case START:
                        play(mPosition);
                        break;
                    case END:
                        pause();
                        break;
                }

            }
        });


        return view;
    }

    private void play(int songPosition) {
        try {
            songPlayerServiceConnection.play(songPosition);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void pause(){
        try {
            songPlayerServiceConnection.mISongManager.pause();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void stop(){
        try {
            songPlayerServiceConnection.mISongManager.stop();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
