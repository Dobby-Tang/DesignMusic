package com.example.android.designmusic.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.designmusic.ISongManager;
import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.task.LoadingMusicTask;

import java.util.ArrayList;

/**
*@author By Dobby Tang
*Created on 2016-03-23 15:22
*/
public class PlayingListAdapter extends BaseListAdapter<Song> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    public ArrayList<Song> playingList;
    public Context context;

    public View mHeaderView;

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View playingItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playing_list_item,parent,false);
        return new PlayingListHolder(playingItem);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder Holder, int Realposition, Song data) {
        if (Holder instanceof PlayingListHolder){
            ((PlayingListHolder)Holder).songName.setText(data.song.get(LoadingMusicTask.songName));
            ((PlayingListHolder)Holder).artistName
                    .setText(data.song.get(LoadingMusicTask.artistName));
            if (data.song.get(LoadingMusicTask.isPlaying)
                    .equals(LoadingMusicTask.isPlaying_TRUE)){
                ((PlayingListHolder)Holder).songName.setTextColor(R.attr.colorPrimaryDark);
                ((PlayingListHolder)Holder).artistName.setTextColor(R.attr.colorPrimaryDark);
                ((PlayingListHolder)Holder).isPlayingImg.setVisibility(View.VISIBLE);
            }else {
                ((PlayingListHolder)Holder).songName.setTextColor(0xff666666);
                ((PlayingListHolder)Holder).artistName.setTextColor(0xff888888);
                ((PlayingListHolder)Holder).isPlayingImg.setVisibility(View.GONE);
            }
        }
    }


    public class PlayingListHolder extends RecyclerView.ViewHolder {
        TextView songName;
        TextView artistName;
        ImageView isPlayingImg;
//        ImageView deleteItem;

        public PlayingListHolder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView){
                return;
            }
            songName = (TextView) itemView.findViewById(R.id.playing_item_music_name);
            artistName = (TextView) itemView.findViewById(R.id.playing_item_artist_name);
            isPlayingImg = (ImageView) itemView.findViewById(R.id.playing_item_album_img);
//            deleteItem = ()
        }
    }

}
