package com.example.android.designmusic.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.example.android.designmusic.ui.activity.AlbumSongActivity;
import com.example.android.designmusic.ui.adapter.AlbumListAdapter;
import com.example.android.designmusic.ui.adapter.BaseListAdapter;
import com.example.android.designmusic.utils.DividerGridItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;

/**
*@author By Dobby Tang
*Created on 2016-04-11 15:25
*/
public class AlbumFragment extends Fragment{
    public static final String TYPE_ALBUM = "album";      //专辑标志位
    public static final String SONG_LIST = "song_list";

    public static AlbumListAdapter albumListAdapter;

    public static AlbumFragment newInstance(String Type) {
        Bundle args = new Bundle();
        AlbumFragment fragment = new AlbumFragment();
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
        setupHomeList(TYPE_ALBUM,mHomeList);
        return mHomeList;
    }

    private void setupHomeList(String mType,RecyclerView mHomeList){
        LoadingMusicTask musicTask;
        if(albumListAdapter == null){
            albumListAdapter = new AlbumListAdapter();
        }
        albumListAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                HashMap<String,String> album = (HashMap<String,String>) data;
                Intent intent = new Intent(getActivity(), AlbumSongActivity.class);
                intent.putExtra(LoadingMusicTask.albumId
                        , album.get(LoadingMusicTask.albumId));
                intent.putExtra(LoadingMusicTask.albumName
                        ,album.get(LoadingMusicTask.albumName));
                intent.putExtra(LoadingMusicTask.artistName
                        ,album.get(LoadingMusicTask.artistName));
                intent.putExtra(LoadingMusicTask.songNumber
                        ,album.get(LoadingMusicTask.songNumber));
                if (SongFragment.songListAdapter != null){
                    ArrayList<Song> songList = SongFragment.songListAdapter.getData();
                    intent.putExtra(SONG_LIST,songList);
                }
                getActivity().startActivity(intent);
            }

        });
        mHomeList.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));
        mHomeList.setAdapter(albumListAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DividerGridItemDecoration alubmDecoration = new DividerGridItemDecoration(16);
            mHomeList.addItemDecoration(alubmDecoration);
        }

        musicTask = new LoadingMusicTask(mType,getActivity());
        musicTask.execute();
    }

}

