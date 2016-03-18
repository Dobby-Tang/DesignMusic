package com.example.android.designmusic.player.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.android.designmusic.ISongManager;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.player.Receiver.RemoteControlReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {

    private final static String TAG = "MusicService";
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

    public List<Song> mSongList = new ArrayList<Song>();
    private MediaPlayer mPlayer;


    private final ISongManager.Stub mBinder = new ISongManager.Stub() {
        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public List<Song> getSongList() throws RemoteException {
            return mSongList;
        }

        @Override
        public void initSongList(List<Song> songList) throws RemoteException {
            mSongList = songList;

        }

        @Override
        public void addSong(Song song) throws RemoteException {
            if(!mSongList.contains(song)){
                mSongList.add(song);
            }
        }

        @Override
        public void play(int songPosition) throws RemoteException{

            if (mSongList != null && mSongList.size() > 0){
                mPlayer.stop();
                mPlayer.reset();
                try{
                    mPlayer.setDataSource(mSongList.get(songPosition).song.get(songPath));
                    mPlayer.prepare();
                }catch (IOException e){
                    e.printStackTrace();
                }

                mPlayer.start();
            }
        }

        @Override
        public void stop() throws RemoteException {
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
        }

        @Override
        public void pause() throws RemoteException {

        }

        /**
         * 权限验证，只允许指定包名的apk访问
         *@author By Dobby Tang
         *Created on 2016-03-14 10:52
         */
//        @Override
//        public boolean onTransact(int code, Parcel data,Parcel reply,int flags)throws RemoteException{
//            String packageName = null;
//            String[] packages = MusicService.this.getPackageManager().
//                    getPackagesForUid(Binder.getCallingUid());
//            if (packages != null && packages.length > 0){
//                packageName = packages[0];
//            }
//            Log.d(TAG,"onTransact:" + packageName);
//            if(!PACKAGE_SAYHI.equals(packageName)){
//                return false;
//            }
//            return super.onTransact(code, data, reply, flags);
//        }
    };


    @Override
    public void onCreate() {
        mPlayer = new MediaPlayer();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"服务器已启动");
        return mBinder;
    }

    private int registerAudioFocus(){

        return 1;
    }

    private AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    private ComponentName mComponentName = new ComponentName(getPackageName()
            ,RemoteControlReceiver.class.getName());

    private AudioManager.OnAudioFocusChangeListener afChangeListener = new
            AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){

            }else if (focusChange == AudioManager.AUDIOFOCUS_GAIN){

            }else if (focusChange== AudioManager.AUDIOFOCUS_LOSS){
                audioManager.unregisterMediaButtonEventReceiver(mComponentName);
                audioManager.abandonAudioFocus(afChangeListener);
            }
        }
    };



}
