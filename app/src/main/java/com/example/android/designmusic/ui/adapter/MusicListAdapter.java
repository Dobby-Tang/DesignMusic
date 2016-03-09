package com.example.android.designmusic.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.designmusic.R;
import com.example.android.designmusic.model.local.LoadingMusicTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
*@author By Dobby Tang
*Created on 2016-03-08 13:53
*/
public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicListHolder>{
    private Context context;
    private ArrayList<HashMap<String,String>> musicList;

    public MusicListAdapter(Context context, ArrayList<HashMap<String,String>> musicList){
        this.context = context;
        this.musicList = musicList;
    }

    @Override
    public MusicListAdapter.MusicListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View musicItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_list_item,parent,false);
        return new MusicListHolder(musicItem);
    }

    @Override
    public void onBindViewHolder(MusicListAdapter.MusicListHolder holder, int position) {
        HashMap<String,String> Song;
        Song = musicList.get(position);
        int userId = Integer.parseInt(Song.get(LoadingMusicTask.musicId));
        int albumId = Integer.parseInt(Song.get(LoadingMusicTask.albumId));

        holder.musicName.setText(musicList.get(position).get(LoadingMusicTask.musicName));
        holder.artistName.setText(musicList.get(position).get(LoadingMusicTask.artistName));
//        holder.musicImage.setImageBitmap(AlbumCoverHelper.getArtwork(context,userId,albumId,true,false));
    }

    @Override
    public int getItemCount() {
        return musicList == null ? 0 : musicList.size();
    }

    public static class MusicListHolder extends RecyclerView.ViewHolder{

        TextView musicName;
        TextView artistName;
        ImageView musicImage;
        ImageView itemMore;

        public MusicListHolder(View itemView) {
            super(itemView);
            musicName = (TextView) itemView.findViewById(R.id.music_item_music_name);
            artistName = (TextView) itemView.findViewById(R.id.music_item_artist_name);
            musicImage = (ImageView) itemView.findViewById(R.id.music_item_album_img);
            itemMore = (ImageView) itemView.findViewById(R.id.music_item_more);

        }
    }
}
