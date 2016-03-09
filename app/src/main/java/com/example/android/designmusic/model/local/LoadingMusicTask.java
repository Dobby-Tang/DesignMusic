package com.example.android.designmusic.model.local;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.example.android.designmusic.ui.adapter.MusicListAdapter;
import com.example.android.designmusic.ui.fragment.HomeFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
*@author By Dobby Tang
*Created on 2016-03-08 13:53
*/
public class LoadingMusicTask extends AsyncTask<Void,Void,Boolean>{

    public static final String musicId = "musicId";               //音乐ID
    public static final String musicName = "musicName";           //音乐名称
    public static final String artistName = "artistName";         //艺术家名称
    public static final String albumName = "albumName";           //专辑名称
    public static final String musicPath = "musicPath";                     //歌曲路径
    public static final String duration_t = "duration_t";         //音乐时长（毫秒）
    public static final String duration = "duration";             //音乐时长

    public static final String artistId = "artistId";             //艺术家ID
    public static final String musicNumber = "musicNumber";        //音乐序号

    public static final String albumId = "albumId";               //专辑ID
    public static final String albumArt = "albumArt";             //专辑图片

    public static final String musicList = "musicList";            //音乐队列
    public static final String playPosition = "playPosition";      //列表list


    private ArrayList<HashMap<String,String >> List;

    private String Type;
    private Context context;
    private RecyclerView mRecyclerView;
    private LayoutInflater mInflater;
    private FragmentManager fragmentManager;


    public LoadingMusicTask(String Type, Context context, RecyclerView mRecyclerView,
                            LayoutInflater mInflater, FragmentManager fragmentManager){
        this.Type = Type;
        this.context = context;
        this.mRecyclerView = mRecyclerView;
        this.mInflater = mInflater;
        this.fragmentManager = fragmentManager;
    }




    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if(aBoolean){
            switch (Type){
                case HomeFragment.TYPE_SONG:
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                    mRecyclerView.setAdapter(new MusicListAdapter(context,List));
                    break;
                case HomeFragment.TYPE_ARTIST:
                    break;
                case HomeFragment.TYPE_ALBUM:
                    break;

            }
        }else{

        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        List = new ArrayList<>();
        switch(Type){
            case HomeFragment.TYPE_SONG:
                List = getMusicFromProvider();
                break;
            case HomeFragment.TYPE_ARTIST:
                List = getArtistFromProvider();
                break;
            case HomeFragment.TYPE_ALBUM:
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
    public ArrayList<HashMap<String,String>> getMusicFromProvider(){
        ArrayList<HashMap<String,String>> musicList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        int time;
        try{
            Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            HashMap<String,String> item;
            while (cursor.moveToNext()){
                item = new HashMap<>();
                item.put(musicId,cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));

                time = Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)))/1000;

                item.put(musicName,cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                item.put(artistName,cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                item.put(albumName,cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                item.put(albumId,cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                item.put(duration,TimeConversions(time));
                item.put(duration_t,cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                item.put(musicPath,cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));

                musicList.add(item);
            }
            return  musicList;
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
            Cursor cursor = resolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,null,null,null,
                    MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
            HashMap<String ,String> item;

            while(cursor.moveToNext()){
                item = new HashMap<>();

                item.put(albumId,cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)));
                item.put(albumName,cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
                item.put(artistName,cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
                item.put(musicNumber,cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
                item.put(albumArt,cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));

                albumsList.add(item);
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
            Cursor cursor = resolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,null,null,null,
                    MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);
            HashMap<String,String> item;
            while (cursor.moveToNext()){
                item = new HashMap<>();

                item.put(artistId,cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists._ID)));
                item.put(artistName,cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)));
                item.put(musicNumber,cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)));
                artistsList.add(item);
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
