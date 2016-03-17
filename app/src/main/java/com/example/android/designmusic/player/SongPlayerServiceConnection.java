package com.example.android.designmusic.player;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.example.android.designmusic.ISongManager;
import com.example.android.designmusic.entity.Song;

import java.util.ArrayList;

/**
 * Created by KJHmobileAdmin on 2016-03-17.
 */
public class SongPlayerServiceConnection implements ServiceConnection {

    public ISongManager mISongManager;
    private ArrayList<Song> songList;
    private int songPosition;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mISongManager = ISongManager.Stub.asInterface(service);
        try {
            mISongManager.initSongList(songList);
            mISongManager.play(songPosition);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mISongManager = null;
    }

    public void initSongList(ArrayList<Song> SongList,int songPosition) throws RemoteException {
        this.songList = SongList;
        this.songPosition = songPosition;
    }

    public void play(int songPosition) throws RemoteException{
        mISongManager.play(songPosition);
    }


}
