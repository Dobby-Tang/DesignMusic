package com.example.android.designmusic.player.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.example.android.designmusic.IAudioStatusChangeListener;
import com.example.android.designmusic.ISongManager;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.player.Receiver.RemoteControlReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {

    public final static int PAUSED = 0;
    public final static int PLAYING = 1;
    public final static int STOP = 2;
    public int state = STOP;

    private static final String TAG = "MusicService";
    private static final String PACKAGE_SAYHI = "com.example.android.designmusic";
    private static boolean isSameList;
    private static final String MY_SHARED = "nowPlayingPosition";

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

    public static final String POSITION = "position";         //正在播放曲目序号

    public List<Song> mSongList = new ArrayList<>();
    private MediaPlayer mPlayer;

    private int nowPlayingPosition = -1;

    private SharedPreferences shared ;
    private SharedPreferences.Editor editor;

    private RemoteCallbackList<IAudioStatusChangeListener> mStatusListener
            = new RemoteCallbackList<>();

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
            if (!mSongList.equals(songList)) {
                mSongList = songList;
                isSameList = false;
            }else{
                isSameList = true;
            }

        }

        @Override
        public void addSong(Song song) throws RemoteException {
            if(!mSongList.contains(song)){
                mSongList.add(song);
            }
        }

        @Override
        public int getSongItem() throws RemoteException {
            return shared.getInt(MY_SHARED,-1);
        }

        @Override
        public void play(int songPosition) throws RemoteException{
            if (requestAudioFocus() == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                if (!isSameList){
                    Log.d(TAG,"nowplayingPosition is : "+nowPlayingPosition+" songPosition is : "+
                            songPosition);
                    nowPlayingPosition = songPosition;
                    if(state == PAUSED){
                        state = PLAYING;
                        mPlayer.start();
                    }else if(state == STOP){
                        state = PLAYING;
                        mPlayer.reset();
                        playingSetting(nowPlayingPosition);
                        mPlayer.start();
                    }
                    editor.putInt(MY_SHARED,songPosition);
                    editor.commit();

                }

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
            if(mPlayer.isPlaying()){
                state = PAUSED;
                mPlayer.pause();
            }
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            if (mPlayer != null){
                if (mPlayer.isPlaying()){
                    return true;
                }
            }
            return false;
        }

        @Override
        public void registerCallBack(IAudioStatusChangeListener mListener) throws RemoteException {
            mStatusListener.register(mListener);

        }

        @Override
        public void unregisterCallBack(IAudioStatusChangeListener mListener) throws RemoteException {
            mStatusListener.unregister(mListener);
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
        super.onCreate();
        mPlayer = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mComponentName = new ComponentName(getPackageName()
                ,RemoteControlReceiver.class.getName());
        shared = getSharedPreferences(MY_SHARED,0);
        editor = shared.edit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"服务器已启动");
        return mBinder;
    }



    public void playingSetting(int position){
        mPlayer.reset();
        try {
            mPlayer.setDataSource(mSongList.get(position).song.get(songPath));
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取播放焦点
    *@author By Dobby Tang
    *Created on 2016-03-18 14:13
    */
    private int requestAudioFocus(){
        int result = audioManager.requestAudioFocus(afChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        return result;
    }

    private AudioManager audioManager;
    private ComponentName mComponentName ;

    /**
     * 管理音频焦点事件
    *@author By Dobby Tang
    *Created on 2016-03-18 15:08
    */
    private AudioManager.OnAudioFocusChangeListener afChangeListener = new
            AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            int listenerNum = mStatusListener.beginBroadcast();
            IAudioStatusChangeListener listener = null;
            if(listenerNum > 0) {
                listener = mStatusListener.getBroadcastItem(0);
            }

            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
                state = PAUSED;
                mPlayer.pause();
                if (listener != null){
                    try {
                        listener.AudioIsPause();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }else if (focusChange == AudioManager.AUDIOFOCUS_GAIN){
                state = PLAYING;
                mPlayer.start();
                if (listener != null){
                    try {
                        listener.AudioIsPlaying();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }else if (focusChange== AudioManager.AUDIOFOCUS_LOSS){
                state = STOP;
                audioManager.unregisterMediaButtonEventReceiver(mComponentName);
                audioManager.abandonAudioFocus(afChangeListener);
                mPlayer.stop();
                mPlayer.reset();
                mPlayer.release();
                if (listener != null){
                    try {
                        listener.AudioIsStop();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            }
            mStatusListener.finishBroadcast();
        }
    };



}
