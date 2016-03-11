package com.example.android.designmusic.ui.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
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
*@author By Dobby Tang
*Created on 2016-03-10 16:51
*/
public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumListHolder>{

    private Context context;
    private ArrayList<HashMap<String,String>> homeList;

    public AlbumListAdapter(Context context, ArrayList<HashMap<String,String >> homeList){
        this.context = context;
        this.homeList = homeList;
    }

    @Override
    public AlbumListAdapter.AlbumListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View homeList = LayoutInflater.from(context).
                inflate(R.layout.home_album_list_item,parent,false);
        return new AlbumListHolder(homeList);
    }

    @Override
    public void onBindViewHolder(AlbumListAdapter.AlbumListHolder holder, int position) {
        HashMap<String,String> Home = homeList.get(position);
        int albumId = Integer.parseInt(Home.get(LoadingMusicTask.albumId));
        Uri uri = ContentUris.withAppendedId(LoadingMusicTask.albumArtUri,albumId);

        holder.itemTitle.setText(Home.get(LoadingMusicTask.albumName));
        holder.itemSubTitle.setText(Home.get(LoadingMusicTask.artistName));
        holder.itemImg.setImageURI(uri);
    }


    @Override
    public int getItemCount() {
        return homeList == null ? 0 : homeList.size();
    }

    public class AlbumListHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView itemImg;
        TextView itemTitle;
        TextView itemSubTitle;
        ImageView itemMore;
        public AlbumListHolder(View itemView) {
            super(itemView);
            itemImg = (SimpleDraweeView) itemView.findViewById(R.id.home_album_item_img);
            itemTitle = (TextView) itemView.findViewById(R.id.home_album_item_title);
            itemSubTitle = (TextView) itemView.findViewById(R.id.home_album_item_subtitle);
            itemMore = (ImageView) itemView.findViewById(R.id.home_album_item_more);
        }
    }
}
