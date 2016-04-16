package com.example.android.designmusic;

import android.app.Application;
import android.content.Intent;

import com.example.android.designmusic.player.service.MusicService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
*@author By Dobby Tang
*Created on 2016-04-15 14:57
*/
public class MedicalApp extends Application{
    ExecutorService cachedThreadPool;
    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(MedicalApp.this, MusicService.class);
        startService(intent);
        cachedThreadPool = Executors.newCachedThreadPool();
    }

    public void execute(Runnable runanble){
        cachedThreadPool.execute(runanble);
    }

}
