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
import com.example.android.designmusic.ui.activity.ArtistSongActivity;
import com.example.android.designmusic.ui.adapter.ArtistListAdapter;
import com.example.android.designmusic.ui.adapter.BaseListAdapter;
import com.example.android.designmusic.utils.DividerGridItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;

/**
*@author By Dobby Tang
*Created on 2016-04-11 16:54
*/
public class ArtistFragment extends Fragment{

    public static final String TYPE_ARTIST = "artist";    //艺术家

    public static final String SONG_LIST = "album_song_list" ;        //专辑歌曲列表

    public static ArtistListAdapter artistListAdapter;

    public static ArtistFragment newInstance(String Type) {
        Bundle args = new Bundle();
        ArtistFragment fragment = new ArtistFragment();
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
        setupHomeList(TYPE_ARTIST,mHomeList);
        return mHomeList;
    }

    private void setupHomeList(String mType,RecyclerView mHomeList){
        LoadingMusicTask musicTask;
        if (artistListAdapter == null){
            artistListAdapter = new ArtistListAdapter(getActivity());
        }

        artistListAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                HashMap<String,String> artist = (HashMap<String,String>) data;
                Intent intent = new Intent(getActivity(), ArtistSongActivity.class);
                intent.putExtra(LoadingMusicTask.artistName
                        ,artist.get(LoadingMusicTask.artistName));
                if (SongFragment.songListAdapter != null){
                    ArrayList<Song> songList = SongFragment.songListAdapter.getData();
                    intent.putExtra(SONG_LIST,songList);
                }
                getActivity().startActivity(intent);
            }
        });

        mHomeList.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));
        mHomeList.setAdapter(artistListAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DividerGridItemDecoration artistDecoration = new DividerGridItemDecoration(16);
            mHomeList.addItemDecoration(artistDecoration);
        }
        musicTask = new LoadingMusicTask(mType,getActivity());
        musicTask.execute();
    }
}
