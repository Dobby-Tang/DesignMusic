package com.example.android.designmusic.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.wnafee.vector.MorphButton;

import java.util.ArrayList;

/**
*@author By Dobby Tang
*Created on 2016-03-15 13:51
*/
public class MusicPlayerFragment extends Fragment{
    private static final String TAG = "MusicPlayerFragment";

    private MorphButton playerBtn;
    private ImageView musicCover;

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
            String str = mPlayingList.get(position).song.get(LoadingMusicTask.songName);
            Toast.makeText(getActivity(),"position="+str,Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_player,container,false);

        musicCover = (ImageView) view.findViewById(R.id.music_player_cover);


        playerBtn = (MorphButton) view.findViewById(R.id.music_player_playBtn);
        playerBtn.setOnStateChangedListener(new MorphButton.OnStateChangedListener() {
            @Override
            public void onStateChanged(MorphButton.MorphState changedTo, boolean isAnimating) {
                switch (changedTo){
                    case START:
                        break;
                }
                Toast.makeText(getActivity(), "Changed to: " + changedTo, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
