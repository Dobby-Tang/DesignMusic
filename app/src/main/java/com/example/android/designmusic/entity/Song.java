package com.example.android.designmusic.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
*待播放列表的音乐实体类
*@author Dobby-Tang
*created at 16/3/13  上午10:26
*/
public class Song implements Parcelable {

    public static final String LOCAL_SONG = "local_song";
    public static final String REMOTE_SONG = "remote_song";

    public HashMap<String,String> song;



    public Song(HashMap<String,String> song){
        this.song = song;
    }


    protected Song(Parcel in) {
        song = in.readHashMap(HashMap.class.getClassLoader());
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(song);
    }
}
