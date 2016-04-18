package com.example.android.designmusic;

import android.os.Bundle;
import android.os.Message;

import com.example.android.designmusic.ui.activity.BaseActivity;

/**
*@author By Dobby Tang
*Created on 2016-04-18 13:55
*/
public class MainActivityTest extends BaseActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_song);
        initBottomView();
    }

    @Override
    protected void handleMessageCallback(Message msg) {

    }

    @Override
    protected void initISongManager(ISongManager mISongManager) {

    }

    @Override
    protected void bottomIsVisibility(boolean isVisibility, int height) {

    }
}
