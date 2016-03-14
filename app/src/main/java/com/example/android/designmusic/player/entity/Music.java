package com.example.android.designmusic.player.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
*待播放列表的音乐实体类
*@author Dobby-Tang
*created at 16/3/13  上午10:26
*/
public class Music implements Parcelable {
    public HashMap<String,String> music;
    public String musicType;

    public Music(HashMap<String,String> music,  String musicType){
        this.music = music;
        this.musicType = musicType;
    }


    protected Music(Parcel in) {
        music = in.readHashMap(HashMap.class.getClassLoader());
        musicType = in.readString();
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeMap(music);
        dest.writeString(musicType);
    }
}
