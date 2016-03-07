package com.example.android.designmusic.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by KJHmobileAdmin on 2016-03-07.
 */
public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicListHolder>{
    @Override
    public MusicListAdapter.MusicListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MusicListAdapter.MusicListHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MusicListHolder extends RecyclerView.ViewHolder{

        public MusicListHolder(View itemView) {
            super(itemView);
        }
    }
}
