package com.example.android.designmusic.ui.fragment;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.designmusic.MainActivity;
import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.example.android.designmusic.ui.activity.MusicPlayerActivity;
import com.example.android.designmusic.ui.adapter.BaseListAdapter;
import com.example.android.designmusic.ui.adapter.SongListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
*@author By Dobby Tang
*Created on 2016-04-11 15:05
*/
public class SongFragment extends Fragment{

    public static final String TYPE_SONG = "music";      //音乐队列

    public static SongListAdapter songListAdapter;

    public static HashMap<String,String> artistImgPathMap;

    public static SongFragment newInstance(String Type) {
        Bundle args = new Bundle();
        SongFragment fragment = new SongFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView mHomeList = (RecyclerView) inflater
                .inflate(R.layout.fragment_home_list,container,false);
        setupHomeList(TYPE_SONG,mHomeList);
        return mHomeList;
    }

    private void setupHomeList(String mType,RecyclerView mHomeList){
        LoadingMusicTask musicTask;
        if(songListAdapter == null){
            songListAdapter = new SongListAdapter();
        }
        songListAdapter.setOnItemClickListener(
                new BaseListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, Object data) {
                        Intent intent = new Intent(getActivity(), MusicPlayerActivity.class);
                        intent.putExtra(MainActivity.PLAYIONG_POSITION,position);
                        intent.putExtra(MainActivity.PLAYIONG_LIST
                                ,(ArrayList<Song>) songListAdapter.getData());
                        getActivity().startActivity(intent);
                    }
                });
        mHomeList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mHomeList.setAdapter(songListAdapter);
        musicTask = new LoadingMusicTask(mType,getActivity());
        musicTask.execute();
    }
}
