package com.example.android.designmusic.ui.adapter;

import android.content.ContentUris;
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

import java.util.HashMap;

/**
*@author By Dobby Tang
*Created on 2016-03-10 16:51
*/
public class AlbumListAdapter extends BaseListAdapter<HashMap<String,String>>{

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View homeList = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.home_album_list_item,parent,false);
        return new AlbumListHolder(homeList);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder holder, int Realposition, HashMap<String, String> data) {
       if (holder instanceof AlbumListHolder){
           int albumId = Integer.parseInt(data.get(LoadingMusicTask.albumId));
           Uri uri = ContentUris.withAppendedId(LoadingMusicTask.albumArtUri,albumId);

           ((AlbumListHolder)holder).itemTitle.setText(data.get(LoadingMusicTask.albumName));
           ((AlbumListHolder)holder).itemSubTitle.setText(data.get(LoadingMusicTask.artistName));
           ((AlbumListHolder)holder).itemImg.setImageURI(uri);
       }
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
