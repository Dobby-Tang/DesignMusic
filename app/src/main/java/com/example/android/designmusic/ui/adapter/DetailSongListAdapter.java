package com.example.android.designmusic.ui.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.task.LoadingMusicTask;

/**
*@author By Dobby Tang
*Created on 2016-03-31 10:46
*/
public class DetailSongListAdapter extends BaseListAdapter<Song>{
    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_detail_listview_item,parent,false);
        return new SongListHolder(itemView);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder holder, int Realposition, Song data) {
        ((SongListHolder)holder).songName.setText(data.song.get(LoadingMusicTask.songName));
        ((SongListHolder)holder).artistName.setText(data.song.get(LoadingMusicTask.artistName));
    }


    public static class SongListHolder extends RecyclerView.ViewHolder{
        TextView songName;
        TextView artistName;
        ImageView itemMore;

        public SongListHolder(View itemView) {
            super(itemView);
            songName= (TextView) itemView.findViewById(R.id.music_detail_item_music_name);
            artistName =(TextView) itemView.findViewById(R.id.music_detail_item_artist_name);
            itemMore = (ImageView) itemView.findViewById(R.id.music_detail_item_more);
        }
    }
}
