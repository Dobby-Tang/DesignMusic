package com.example.android.designmusic.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
*@author By Dobby Tang
*Created on 2016-03-25 15:56
*/
public abstract class BaseListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final int TYEP_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    private View mHeaderView;

    private ArrayList<T> mDatas = new ArrayList<>();

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener l){
        mListener = l;
    }

    public void setHeaderView(View headerView){
        mHeaderView = headerView;
    }

    public void setDatas(ArrayList<T> datas){
        mDatas = datas;
        notifyDataSetChanged();
    }

    public ArrayList<T> getData(){
        return  mDatas;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null){
            return TYPE_NORMAL;
        }
        if (position == 0){
            return TYEP_HEADER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYEP_HEADER) {
            return new Holder(mHeaderView);
        }
        return onCreate(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYEP_HEADER){
            return;
        }

        final int pos = getRealPosition(holder);
        final T data = mDatas.get(pos);
        onBind(holder,pos,data);

        if (mListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(pos,data);
                }
            });
        }
    }


    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mDatas.size() : mDatas.size()+1 ;
    }


    public abstract RecyclerView.ViewHolder onCreate(ViewGroup parent,final  int viewType);
    public abstract void onBind(RecyclerView.ViewHolder holder,int Realposition,T data);


    public class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener<T> {
        void onItemClick(int position,T data);
    }
}
