package com.example.android.designmusic.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.player.Constant;
import com.example.android.designmusic.ui.fragment.AlbumFragment;
import com.example.android.designmusic.ui.fragment.ArtistFragment;
import com.example.android.designmusic.ui.fragment.SongFragment;
import com.example.android.designmusic.utils.FormatTime;

import java.util.ArrayList;
import java.util.HashMap;

/**
*@author By Dobby Tang
*Created on 2016-03-08 13:53
*/
public class LoadingMusicTask extends AsyncTask<Void,Void,Boolean>{

    public static final String songId = "songId";               //音乐ID
    public static final String songName = "songName";           //音乐名称
    public static final String artistName = "artistName";         //艺术家名称
    public static final String albumName = "albumName";           //专辑名称
    public static final String songPath = "songPath";                     //歌曲路径
    public static final String duration_t = "duration_t";         //音乐时长（毫秒）
    public static final String duration = "duration";             //音乐时长

    public static final String artistId = "artistId";             //艺术家ID
    public static final String songNumber = "songNumber";        //音乐序号
    public static final String albumNum = "albumNum";            //专辑数量
    public static final String artistImgPathID = "artistImgPath";   //艺术家图片

    public static final String albumId = "albumId";               //专辑ID
    public static final String albumArt = "albumArt";             //专辑图片

    public static final String isPlaying = "isPlaying";           //是否播放
    public static final String isPlaying_TRUE = "true";
    public static final String isPlaying_FALSE = "false";

    public static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");


    private ArrayList<HashMap<String,String >> List;
    private ArrayList<Song> songList;          //歌曲队列

    private String Type;
    private Context context;

    private HashMap<String,String> artistImgPathMap;

    private static final String TAG = "LoadingMusicTask";

    public LoadingMusicTask(String Type, Context context){
        this.Type = Type;
        this.context = context;
    }

    public HashMap<String,String> getArtistImgPathMap(){
        return artistImgPathMap;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if(aBoolean){
            switch (Type){
                case SongFragment.TYPE_SONG:
                    SongFragment.artistImgPathMap = artistImgPathMap;
                    SongFragment.songListAdapter.setDatas(songList);
                    SongFragment.songListAdapter.notifyDataSetChanged();
                    break;
                case ArtistFragment.TYPE_ARTIST:
                    ArtistFragment.artistListAdapter.setDatas(List);
                    ArtistFragment.artistListAdapter.notifyDataSetChanged();
                    break;
                case AlbumFragment.TYPE_ALBUM:
                    AlbumFragment.albumListAdapter.setDatas(List);
                    AlbumFragment.albumListAdapter.notifyDataSetChanged();
                    break;

            }
        }else{

        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        List = new ArrayList<>();
        switch(Type){
            case SongFragment.TYPE_SONG:
                songList = getMusicFromProvider();
                break;
            case ArtistFragment.TYPE_ARTIST:
                List = getArtistFromProvider();
                break;
            case AlbumFragment.TYPE_ALBUM:
                List = getAlbumFromProvider();
                break;
            default:
                break;
        }
        if(List == null){
            return false;
        }else{
            return true;
        }
    }

    //在媒体库中获取所有音乐信息
    public ArrayList<Song> getMusicFromProvider(){
        ArrayList<Song> songList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        int time;
        try{
            Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            HashMap<String,String> item;
            artistImgPathMap = new HashMap<>();
            while (cursor.moveToNext()){
                item = new HashMap<>();
                item.put(songId,cursor.getString(cursor.getColumnIndex(
                        MediaStore.Audio.Media._ID)));
                time = Integer.parseInt(cursor.getString(
                        cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)))/1000;

                if (time > 30){
                    item.put(songName,cursor.getString(
                            cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    item.put(artistName,cursor.getString(
                            cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                    item.put(artistId,cursor.getString(
                            cursor.getColumnIndex(MediaStore.Audio.Artists._ID)));
                    item.put(albumName,cursor.getString(
                            cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                    item.put(albumId,cursor.getString(
                            cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                    item.put(duration,FormatTime.secToTime(time));
                    item.put(duration_t,cursor.getString(
                            cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                    item.put(songPath,cursor.getString(
                            cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                    item.put(isPlaying,isPlaying_FALSE);

                    if (!artistImgPathMap.containsKey(item.get(artistName))){
                        artistImgPathMap.put(albumId,item.get(albumId));
                        artistImgPathMap.put(artistName,item.get(artistName));
                    }

                    Song song = new Song(item);
                    songList.add(song);
                }

            }
            return  songList;
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }


    //在媒体库中获取所有专辑信息
    public ArrayList<HashMap<String,String>> getAlbumFromProvider(){
        ArrayList<HashMap<String,String>> albumsList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        try{
            Cursor cursor = resolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,null,null
                    ,null,MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
            HashMap<String ,String> item;

            while(cursor.moveToNext()){
                item = new HashMap<>();

                item.put(albumId,cursor.getString(cursor.getColumnIndex(
                        MediaStore.Audio.Albums._ID)));
                String name = cursor.getString(cursor.getColumnIndex(
                        MediaStore.Audio.Albums.ALBUM));
                item.put(albumName,name);
                item.put(artistName,cursor.getString(cursor.getColumnIndex(
                        MediaStore.Audio.Albums.ARTIST)));
                item.put(songNumber,cursor.getString(cursor.getColumnIndex(
                        MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
                item.put(albumArt,cursor.getString(cursor.getColumnIndex(
                        MediaStore.Audio.Albums.ALBUM_ART)));

                if (!name.equals(Constant.UN_ALBUM)){
                    albumsList.add(item);
                }
            }
            return albumsList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    //在媒体库中获取所有歌手信息
    public ArrayList<HashMap<String,String>> getArtistFromProvider(){
        ArrayList<HashMap<String,String>> artistsList= new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        try{
            Cursor cursor = resolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,null,null
                    ,null,MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);
            HashMap<String,String> item;
            while (cursor.moveToNext()){
                item = new HashMap<>();

                item.put(artistId,cursor.getString(cursor.getColumnIndex(
                        MediaStore.Audio.Artists._ID)));
                String name = cursor.getString(cursor.getColumnIndex(
                        MediaStore.Audio.Artists.ARTIST));
                item.put(artistName,name);
                item.put(albumNum,cursor.getString(cursor.getColumnIndex(
                        MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)));
//                if (SongFragment.artistImgPathMap != null &&
//                        item.get(artistName).equals(artistImgPathMap.get(artistName))){
//                    item.put(artistImgPathID,artistImgPathMap.get(albumId));
//                }
                if (!name.equals(Constant.UN_ARTIST)){
                    artistsList.add(item);
                }
            }
            return  artistsList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }




    //转换时间
    public String TimeConversions(int time){
        int minute = time / 60;
        time = time % 60;
        String format_duration;
        if(minute < 10 && time <10){
            format_duration = "0" + minute + ":" +"0" + time;
        }
        else if(minute <10 && time >10){
            format_duration = "0" +minute + ":" + time;
        }
        else if (minute >10 && time < 10){
            format_duration = minute + ":" + "0" + time;
        }
        else{
            format_duration = minute + ":" + time;
        }
        return format_duration;
    }
}
