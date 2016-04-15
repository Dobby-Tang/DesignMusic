package com.example.android.designmusic;

import android.app.Application;

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
        cachedThreadPool = Executors.newCachedThreadPool();
    }

    public void execute(Runnable runanble){
        cachedThreadPool.execute(runanble);
    }

}
