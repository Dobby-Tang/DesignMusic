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

import java.util.HashMap;

/**
*@author By Dobby Tang
*Created on 2016-03-11 16:52
*/
public class ArtistListAdapter extends BaseListAdapter<HashMap<String,String>> {

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View artistItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_artist_list_item,parent,false);
        return new ArtisstListHolder(artistItem);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder holder, int Realposition
            , HashMap<String, String> data) {
        ((ArtisstListHolder)holder).title.setText(data.get(LoadingMusicTask.artistName));
        ((ArtisstListHolder)holder).subTitle.setText(data.get(LoadingMusicTask.songNumber));
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
