package com.example.android.designmusic.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.designmusic.R;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by KJHmobileAdmin on 2016-03-11.
 */
public class ArtistListAdapter extends RecyclerView.Adapter<ArtistListAdapter.ArtisstListHolder> {
    private ArrayList<HashMap<String,String>> artistList;

    public ArtistListAdapter(ArrayList<HashMap<String,String>> artistList){
        this.artistList = artistList;
    }

    @Override
    public ArtisstListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View artistItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_artist_list_item,parent,false);
        return new ArtisstListHolder(artistItem);
    }

    @Override
    public void onBindViewHolder(ArtisstListHolder holder, int position) {
        HashMap<String,String> Artist = artistList.get(position);
        holder.title.setText(Artist.get(LoadingMusicTask.artistName));
        holder.subTitle.setText(Artist.get(LoadingMusicTask.songNumber));
    }


    @Override
    public int getItemCount() {
        return artistList == null ? 0 : artistList.size();
    }


    public void setArtistList(ArrayList<HashMap<String,String>> artistList){
        this.artistList = artistList;
    }

    public class ArtisstListHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView artistImg;
        TextView title;
        TextView subTitle;
        ImageView itemMore;
        public ArtisstListHolder(View itemView) {
            super(itemView);
            artistImg = (SimpleDraweeView) itemView.findViewById(R.id.home_artist_item_img);
            title = (TextView) itemView.findViewById(R.id.home_artist_item_title);
            subTitle = (TextView) itemView.findViewById(R.id.home_artist_item_subtitle);
            itemMore = (ImageView) itemView.findViewById(R.id.home_artist_item_more);
        }
    }
}
