package com.example.android.designmusic.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by KJHmobileAdmin on 2016-03-07.
 */
public class Music implements Parcelable
{
    long id ;                    //音乐ID
    String title ;               //音乐标题
    String artist;               //艺术家
    long duration;               //时长
    long size;                   //文件大小
    String url;                  //文件路径
    String album;                //唱片图片
    long albumId;                //唱片图片ID
//    int isMusic;                 //是否为音乐


    public Music(){
    }

//    public Music(long id,String title,String artist,long duration,long size,
//                 String url,String album,long albumId,int isMusic){
//        this.id = id;
//        this.title = title;
//        this.artist = artist;
//        this.duration = duration;
//        this.size = size;
//        this.url = url;
//        this.album = album;
//        this.albumId
//    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getArtist(){
        return  artist;
    }

    public void setArtist(String artist){
        this.artist = artist;
    }

    public long getDuration(){
        return duration;
    }

    public void setDuration(long duration){
        this.duration = duration;
    }

    public long getSize(){
        return size;
    }

    public void setSize(long size){
        this.size = size;
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getAlbum(){
        return album;
    }

    public void setAlbum(String album){
        this.album = album;
    }
    public long getAlbumId(){
        return albumId;
    }

    public void setAlbumId(long albumId){
        this.albumId = albumId;
    }
//
//    public int getIsMusic(){
//        return  isMusic;
//    }
//
//    public void setIsMusic()

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeLong(duration);
        dest.writeLong(size);
        dest.writeString(url);
        dest.writeString(album);
        dest.writeLong(albumId);
    }

    public static final Parcelable.Creator<Music> CREATOR = new Creator<Music>(){

        @Override
        public Music createFromParcel(Parcel source) {
            Music music = new Music();
            music.setId(source.readLong());
            music.setTitle(source.readString());
            music.setArtist(source.readString());
            music.setDuration(source.readLong());
            music.setSize(source.readLong());
            music.setUrl(source.readString());
            music.setAlbum(source.readString());
            music.setAlbumId(source.readLong());
            return music;
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };


}
