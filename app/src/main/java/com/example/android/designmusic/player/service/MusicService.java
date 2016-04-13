package com.example.android.designmusic.player.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.example.android.designmusic.IAudioStatusChangeListener;
import com.example.android.designmusic.IRefreshCurrentTimeListener;
import com.example.android.designmusic.ISongManager;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.player.Receiver.RemoteControlReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MusicService extends Service {

    public final static int PAUSED = 0;
    public final static int PLAYING = 1;
    public final static int STOP = 2;
    public int state = STOP;

    private static final String TAG = "MusicService";
    private static final String PACKAGE_SAYHI = "com.example.android.designmusic";
    private static boolean isSameList;
    private static final String NOW_PLAYING = "nowPlayingPosition";

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


    public static final String isPlaying = "isPlaying";           //是否播放
    public static final String isPlaying_TRUE = "true";
    public static final String isPlaying_FALSE = "false";

    private static final int PLAYING_REPEAT = 1;               //列表循环
    private static final int PLAYING_REPEAT_ONE = 2;           //单曲循环
    private static final int PLAYING_RANDOM  = 3;              //随机播放

    private int mPlayingMode = PLAYING_REPEAT;

    public List<Song> mSongList ;                  //内部播放器播放顺序
    public List<Song> mPlayList;                  //播放列表
    private MediaPlayer mPlayer;

    private int nowPlayingPosition = -1;
//    private int historyPlayingPosition = -1;       //切换模式时的播放序号

//    private SharedPreferences shared ;
//    private SharedPreferences.Editor editor;

    private RemoteCallbackList<IAudioStatusChangeListener> mStatusListener
            = new RemoteCallbackList<>();

    private RemoteCallbackList<IRefreshCurrentTimeListener> mRefreshTimeListener
            = new RemoteCallbackList<>();

    private final ISongManager.Stub mBinder = new ISongManager.Stub() {
        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public List<Song> getSongList() throws RemoteException {
            return mPlayList;
        }

        @Override
        public void initSongList(List<Song> songList) throws RemoteException {
            if (mSongList == null) {
                Log.d(TAG,"-----> init Song list is mSonglist == null");
                mSongList = new ArrayList<>();
                mPlayList = new ArrayList<>();
                mSongList.addAll(songList);
                mPlayList.addAll(songList);
                isSameList = true;
            }else{
                if (!songListEquals(mPlayList,songList)){
                    Log.d(TAG,"-----> init Song list is mSonglist != songList");
                    mSongList = new ArrayList<>();
                    mPlayList = new ArrayList<>();
                    mSongList.addAll(songList);
                    mPlayList.addAll(songList);
                    isSameList = false;
                }else{
                    Log.d(TAG,"-----> init Song list is mSonglist == songList");
                    isSameList = true;
                }

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
            return nowPlayingPosition;
        }

        @Override
        public int getPlayingMode() throws RemoteException {
            return mPlayingMode;
        }

        @Override
        public void setPlayingMode(int mode) throws RemoteException {
            if (mode == PLAYING_RANDOM){
                mSongList = new ArrayList<>();
                mSongList.addAll(mPlayList);
                Random rnd = new Random(2);
                Collections.shuffle(mSongList,rnd);
                Log.d(TAG, "setPlayingMode: shuffle list ...");
            }else if (mode == PLAYING_REPEAT){
                mSongList = new ArrayList<>();
                mSongList.addAll(mPlayList);
//            }else if (mode == PLAYING_REPEAT_ONE){
//                mSongList = new ArrayList<>();
//                mSongList.add(mPlayList.get(nowPlayingPosition));
            }
            nowPlayingPosition = getSongListPos(getPlayListPos());
            mPlayingMode = mode;
        }

        @Override
        public void play(int songPosition,boolean isListClick) throws RemoteException{
            Log.d(TAG, "play: songPosition is " + songPosition);
            if (isListClick){
                if ( mPlayingMode == PLAYING_REPEAT){
                    player(songPosition);
                }else if(mPlayingMode == PLAYING_REPEAT_ONE){
                    player(songPosition);
                }else if (mPlayingMode == PLAYING_RANDOM ){
                    player(getSongListPos(songPosition));
                }
            }else {
                if (mPlayingMode == PLAYING_REPEAT_ONE){
                    player(nowPlayingPosition);
                }else{
                    player(songPosition);
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
        public void next() throws RemoteException {
            if (mPlayingMode != PLAYING_REPEAT_ONE){
                nextSong();
            }
        }

        @Override
        public void last() throws RemoteException {
            if (mPlayingMode != PLAYING_REPEAT_ONE){
                lastSong();
            }
        }

        @Override
        public void seekTo(int progress) throws RemoteException {
            mPlayer.seekTo(progress);
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
        public boolean isEqualsSongList(List<Song> songList) throws RemoteException {
            return songListEquals(mPlayList,songList);
        }

        @Override
        public void registerAudioCallBack(IAudioStatusChangeListener mListener) throws RemoteException {
            mStatusListener.register(mListener);

        }

        @Override
        public void unregisterAudioCallBack(IAudioStatusChangeListener mListener) throws RemoteException {
            mStatusListener.unregister(mListener);
        }

        @Override
        public void registerCurrentTimeCallBack(IRefreshCurrentTimeListener mListener) throws RemoteException {
            mRefreshTimeListener.register(mListener);
        }

        @Override
        public void unregisterCurrentTimeCallBack(IRefreshCurrentTimeListener mListener) throws RemoteException {
            mRefreshTimeListener.unregister(mListener);
        }

        /**
         * 权限验证，只允许指定包名的apk访问
         *@author By Dobby Tang
         *Created on 2016-03-14 10:52
         */
//        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags)throws RemoteException{
            String packageName = null;
            String[] packages = MusicService.this.getPackageManager().
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
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mComponentName = new ComponentName(getPackageName()
                ,RemoteControlReceiver.class.getName());

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mPlayingMode != PLAYING_REPEAT_ONE){
                    nextSong();
                }else {
                    player(nowPlayingPosition);
                }
            }
        });
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



    public void player(int songPosition){
        if (requestAudioFocus() == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            Log.d(TAG,"now playingPosition is : "+nowPlayingPosition+" songPosition is : "+
                    songPosition);
            Log.d(TAG,"state :" + state);
            if(state == PAUSED && mSongList.get(songPosition).song.get(isPlaying)
                    .equals(isPlaying_TRUE)){
                state = PLAYING;
                mPlayer.start();
            }else if(!isSameList || mSongList.get(songPosition).song.get(isPlaying)
                    .equals(isPlaying_FALSE)) {
                mSongList.get(nowPlayingPosition).song.put(isPlaying,isPlaying_FALSE);
                nowPlayingPosition = songPosition;
                state = PLAYING;
                mPlayer.reset();
                playingSetting(nowPlayingPosition);
                mSongList.get(nowPlayingPosition).song.put(isPlaying,isPlaying_TRUE);
                mPlayer.start();
            }
            int listenerNum = mStatusListener.beginBroadcast();
            for (int i = 0;i < listenerNum;i++){
                IAudioStatusChangeListener listener = getIAudioStatusChangeListener(i);
                if (listener != null){
                    try {
                        setSongIsPlayingFlag(songPosition);
                        listener.playingCallback(getPlayListPos());
                        new Thread(new getPlayingProgress()).start();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            mStatusListener.finishBroadcast();

        }
    }


    public void nextSong(){
        if (nowPlayingPosition >= 0){
            int songPosition = -1;
            if(nowPlayingPosition < mSongList.size() - 1 ){
                songPosition = nowPlayingPosition + 1;
            }else {
                songPosition = 0;
            }
            if (songPosition >= 0){
                state = STOP;
                player(songPosition);
            }

        }
    }

    public void lastSong(){
        if (nowPlayingPosition >= 0){
            int songPosition = -1;
            if(nowPlayingPosition - 1 >= 0 ){
                songPosition = nowPlayingPosition - 1;
            }else {
                songPosition = mSongList.size() - 1;
            }
            if (songPosition >= 0){
                state = STOP;
                player(songPosition);
            }

        }
    }


    public void setSongIsPlayingFlag(int position){
        for (int i = 0 ; i < mSongList.size(); i++){
            if (i == position){
                mSongList.get(i).song.put(isPlaying,isPlaying_TRUE);
            }else {
                mSongList.get(i).song.put(isPlaying,isPlaying_FALSE);
            }
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


    private IAudioStatusChangeListener getIAudioStatusChangeListener(int listenerNum){
        IAudioStatusChangeListener listener = null;
        if(listenerNum >= 0) {
            listener = mStatusListener.getBroadcastItem(listenerNum);
            return listener;
        }
        return null;
    }

    private IRefreshCurrentTimeListener getIRefreshCurrentTimeListener(){
        IRefreshCurrentTimeListener listener = null ;
        int num = mRefreshTimeListener.beginBroadcast();
        if(num > 0 ){
            listener = mRefreshTimeListener.getBroadcastItem(0);
        }
        mRefreshTimeListener.finishBroadcast();
        return listener;
    }



    /**
     * 管理音频焦点事件
    *@author By Dobby Tang
    *Created on 2016-03-18 15:08
    */
    private AudioManager audioManager;
    private ComponentName mComponentName ;

    private AudioManager.OnAudioFocusChangeListener afChangeListener = new
            AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            int listenerNum = mStatusListener.beginBroadcast();
            for (int i = 0;i < listenerNum;i++){
                IAudioStatusChangeListener listener = getIAudioStatusChangeListener(i);
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
                    if(state != PAUSED){
                        state = PLAYING;
                        mPlayer.start();
                        if (listener != null){
                            try {
                                listener.AudioIsPlaying();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }else if (focusChange== AudioManager.AUDIOFOCUS_LOSS){
                    state = PAUSED;
                    mPlayer.pause();
                    if (listener != null){
                        try {
                            listener.AudioIsPause();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
            mStatusListener.finishBroadcast();

        }
    };


    public boolean songListEquals(List<Song> thisList,List<Song> newList) {
        if (thisList != null){
            if (thisList.size() != newList.size()) {
                Log.d(TAG,"songListEquals : " + "size is Unequal" );
                return false;
            }
            for (int i = 0; i < thisList.size(); i++) {
                Log.d(TAG,"thisList ID : " + thisList.get(i).song.get(songId)
                        + "newList ID : " + newList.get(i).song.get(songId));
                if (!thisList.get(i).song.get(songId).equals(newList.get(i).song.get(songId))) {
                    Log.d(TAG, "songListEquals: unequal id is " + thisList.get(i).song.get(songId));
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public int getPlayListPos(){
        for (int i = 0;i<mPlayList.size();i++){
            if (mPlayList.get(i).song.get(isPlaying).equals(isPlaying_TRUE)){
                return i;
            }
        }
        return 0;
    }

    public int getSongListPos(int position){
        for (int i = 0;i < mSongList.size();i++){
            if (mPlayList.get(position).song.get(songName)
                    .equals(mSongList.get(i).song.get(songName))){
                Log.d(TAG, "getSongListPos: " + i );
                return i;
            }
        }
        return 0;
    }

    class getPlayingProgress implements Runnable {

        @Override
        public void run() {
            if (mPlayer != null){
                IRefreshCurrentTimeListener listener = getIRefreshCurrentTimeListener();
                while (mPlayer.isPlaying()){
                    try {
                        if (listener != null){
                            listener.playingCurrentTimeCallback(mPlayer.getCurrentPosition());
                            Thread.sleep(500);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



}
