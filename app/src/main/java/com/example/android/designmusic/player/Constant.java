package com.example.android.designmusic.player;

/**
*@author By Dobby Tang
*Created on 2016-04-15 18:55
*/
public class Constant {

    public static final int AUDIO_PAUSE_CALL_BACK = 0;         //音频焦点暂停播放
    public static final int AUDIO_PLAYING_CALL_BACK = 1;       //音频焦点开始播放
    public static final int PLAYING_CALL_BACK = 2;             //service开始播放回调
    public static final int PLAYING_TIME_CALL_BACK = 3;        //播放进度回调

    public static final int IS_PLAYING = 4;            //正在播放
    public static final int IS_UN_PLAYING = 5;         //暂替（停止）播放
    public static final int ALBUM_SONG_LIST = 6;

    public static final int PLAYING_REPEAT = 7;               //列表循环
    public static final int PLAYING_REPEAT_ONE = 8;           //单曲循环
    public static final int PLAYING_RANDOM  = 9;              //随机播放

    public static final String UN_ARTIST = "<unknown>";   //未知艺术家(不加入音乐库)
    public static final String UN_ALBUM = "Music";     //未知专辑不(加入音乐库)
}
