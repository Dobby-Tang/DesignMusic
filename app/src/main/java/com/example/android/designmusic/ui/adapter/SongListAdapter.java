package com.example.android.designmusic.ui.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.example.android.designmusic.ui.activity.MusicPlayerActivity;
import com.example.android.designmusic.ui.fragment.HomeFragment;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
*@author By Dobby Tang
*Created on 2016-03-08 13:53
*/
public class SongListAdapter extends BaseListAdapter<Song>{


    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View musicItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_music_list_item,parent,false);
        return new MusicListHolder(musicItem,parent.getContext());
    }

    @Override
    public void onBind(RecyclerView.ViewHolder holder, int Realposition, Song data) {
        int albumId = Integer.parseInt(data.song.get(LoadingMusicTask.albumId));

        ((MusicListHolder)holder).musicName.setText(data.song.get(LoadingMusicTask.songName));
        ((MusicListHolder)holder).artistName.setText(data.song.get(LoadingMusicTask.artistName));

        Uri uri = ContentUris.withAppendedId(LoadingMusicTask.albumArtUri,albumId);
        ((MusicListHolder)holder).musicImage.setImageURI(uri);

    }



    public static class MusicListHolder extends RecyclerView.ViewHolder{

        TextView musicName;
        TextView artistName;
        SimpleDraweeView musicImage;
        ImageView itemMore;


        public MusicListHolder(final View itemView, final Context context) {
            super(itemView);
            musicName = (TextView) itemView.findViewById(R.id.music_item_music_name);
            artistName = (TextView) itemView.findViewById(R.id.music_item_artist_name);
            musicImage = (SimpleDraweeView) itemView.findViewById(R.id.music_item_album_img);
            itemMore = (ImageView) itemView.findViewById(R.id.music_item_more);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getLayoutPosition();
//                    Intent intent = new Intent(context, MusicPlayerActivity.class);
//                    intent.putExtra(HomeFragment.PLAYIONG_POSITION,position);
//                    intent.putExtra(HomeFragment.PLAYIONG_LIST,songList);
//                    context.startActivity(intent);
//                }
//            });
        }
    }
}
