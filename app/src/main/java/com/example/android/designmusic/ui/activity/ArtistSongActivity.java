package com.example.android.designmusic.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.example.android.designmusic.ui.adapter.ArtistAlbumListAdapter;
import com.example.android.designmusic.ui.adapter.DetailSongListAdapter;
import com.example.android.designmusic.ui.fragment.HomeFragment;
import com.example.android.designmusic.utils.DividerGridItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;

public class ArtistSongActivity extends AppCompatActivity {

    private static final String TAG = "ArtistSongActivity";

    private static final int TYPE_ARTIST_SONG_LIST = 1;
    private static final int TYPE_ARTIST_ALBUM_LIST = 2;

    private ArrayList<Song> songList;

    private String artistName;

    private RecyclerView albumListView;
    private RecyclerView songListView;

    private ArtistAlbumListAdapter mAlbumListAdapter;
    private DetailSongListAdapter mDetailSongListAdapter;

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TYPE_ARTIST_ALBUM_LIST:
                    ArrayList<HashMap<String,String>> albumList
                            = (ArrayList<HashMap<String,String>>) msg.obj;
                    if (mAlbumListAdapter != null){
                        mAlbumListAdapter.setDatas(albumList);
                        mAlbumListAdapter.notifyDataSetChanged();
                    }
                    break;
                case TYPE_ARTIST_SONG_LIST:
                    ArrayList<Song> songList = (ArrayList<Song>) msg.obj;
                    if (mDetailSongListAdapter != null){
                        mDetailSongListAdapter.setDatas(songList);
                        mDetailSongListAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_song);

        Intent intent = getIntent();
        songList = intent.getParcelableArrayListExtra(HomeFragment.SONG_LIST);
        artistName = intent.getStringExtra(LoadingMusicTask.artistName);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        albumListView = (RecyclerView)findViewById(R.id.detail_album_list) ;
        albumListView.setLayoutManager(new StaggeredGridLayoutManager(1, OrientationHelper.HORIZONTAL));
        DividerGridItemDecoration artistDecoration = new DividerGridItemDecoration(16);
        albumListView.addItemDecoration(artistDecoration);

        songListView = (RecyclerView)findViewById(R.id.detail_song_list);
        songListView.setLayoutManager(new LinearLayoutManager(this));

        mAlbumListAdapter = new ArtistAlbumListAdapter();
        albumListView.setAdapter(mAlbumListAdapter);

        mDetailSongListAdapter = new DetailSongListAdapter();
        songListView.setAdapter(mDetailSongListAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                initArtistDetailList(songList,artistName);
            }
        }).start();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }



    public void initArtistDetailList(ArrayList<Song> songList, String artistName){
        ArrayList<Song> artistSongList = new ArrayList<>();
        ArrayList<HashMap<String,String>> artistAlbumList= new ArrayList<>();
        HashMap<String ,String> item;
        if (songList != null && artistName != null){
            for (int i = 0; i< songList.size();i++){
                if (songList.get(i).song.get(LoadingMusicTask.artistName).equals(artistName)){
                    Song song = songList.get(i);
                    artistSongList.add(song);

                    item = new HashMap<>();
                    item.put(LoadingMusicTask.albumId,song.song.get(LoadingMusicTask.albumId));
                    item.put(LoadingMusicTask.albumName,song.song.get(LoadingMusicTask.albumName));
                    item.put(LoadingMusicTask.artistName
                            ,song.song.get(LoadingMusicTask.artistName));

                    item.put(LoadingMusicTask.songNumber
                            ,song.song.get(LoadingMusicTask.songNumber));
                    item.put(LoadingMusicTask.albumArt,song.song.get(LoadingMusicTask.albumArt));
                    if (!artistAlbumList.contains(item)){
                        artistAlbumList.add(item);
                    }
                }
            }
        }

        Message songListMsg = Message.obtain();
        songListMsg.what = TYPE_ARTIST_SONG_LIST;
        songListMsg.obj = artistSongList;
        mHandler.sendMessage(songListMsg);

        Message albumListMsg = Message.obtain();
        albumListMsg.what = TYPE_ARTIST_ALBUM_LIST;
        albumListMsg.obj = artistAlbumList;
        mHandler.sendMessage(albumListMsg);

    }

}
