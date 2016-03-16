package com.example.android.designmusic.player.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.example.android.designmusic.IMusicManager;
import com.example.android.designmusic.entity.Song;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayerService extends Service {

    private final static String TAG = "MusicPlayerService";
    private static final String PACKAGE_SAYHI = "com.example.android.designmusic";

    public static final String songId = "songId";               //音乐ID
    public static final String songName = "songName";           //音乐名称
    public static final String artistName = "artistName";         //艺术家名称
    public static final String albumName = "albumName";           //专辑名称
    public static final String songPath = "songPath";                     //歌曲路径
    public static final String duration_t = "duration_t";         //音乐时长（毫秒）
    public static final String duration = "duration";             //音乐时长

    public static final String artistId = "artistId";             //艺术家ID
    public static final String songNumber = "songNumber";        //音乐序号

    public static final String albumId = "albumId";               //专辑ID
    public static final String albumArt = "albumArt";             //专辑图片

    private List<Song> songList = new ArrayList<Song>();

    private MediaPlayer mPlayer;

    private final IMusicManager.Stub mBinder = new IMusicManager.Stub() {
        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public List<Song> getSongList() throws RemoteException {
            return songList;
        }

        @Override
        public void addMusic(Song song) throws RemoteException {
            if(!songList.contains(song)){
                songList.add(song);
            }
        }

        @Override
        public void player(int position) throws RemoteException {
//            mPlayer.setDataSource(musicList.get(position).music.get(songPath));
//            mPlayer.prepare();
            mPlayer.start();
        }

        @Override
        public void stop() throws RemoteException {

        }

        @Override
        public void pause() throws RemoteException {

        }

        /**
         * 权限验证，只允许指定包名的apk访问
         *@author By Dobby Tang
         *Created on 2016-03-14 10:52
         */
        @Override
        public boolean onTransact(int code, Parcel data,Parcel reply,int flags)throws RemoteException{
            String packageName = null;
            String[] packages = MusicPlayerService.this.getPackageManager().
                    getPackagesForUid(Binder.getCallingUid());
            if (packages != null && packages.length > 0){
                packageName = packages[0];
            }
            Log.d(TAG,"onTransact:" + packageName);
            if(!PACKAGE_SAYHI.equals(packageName)){
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }
    };



    @Override
    public IBinder onBind(Intent intent) {
        mPlayer = new MediaPlayer();
        return mBinder;
    }

}
