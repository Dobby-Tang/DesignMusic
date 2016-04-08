package com.example.android.designmusic.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
*@author By Dobby Tang
*Created on 2016-04-08 17:13
*/
public class RecyclerViewForNestedScrollView extends RecyclerView{
    public RecyclerViewForNestedScrollView(Context context) {
        super(context);
    }

    public RecyclerViewForNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewForNestedScrollView(Context context, AttributeSet attrs
            , int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
