package com.example.android.designmusic.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.android.designmusic.ISongManager;
import com.example.android.designmusic.MedicalApp;
import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.player.Constant;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.example.android.designmusic.ui.adapter.ArtistAlbumListAdapter;
import com.example.android.designmusic.ui.adapter.ArtistSongListAdapter;
import com.example.android.designmusic.ui.adapter.BaseListAdapter;
import com.example.android.designmusic.ui.fragment.AlbumFragment;
import com.example.android.designmusic.utils.DividerGridItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;

/**
*@author By Dobby Tang
*Created on 2016-04-19 11:07
*/
public class ArtistSongActivity extends BaseActivity{

    private static final int TYPE_ARTIST_SONG_LIST = 10;     //歌曲队列
    private static final int TYPE_ARTIST_ALBUM_LIST = 11;    //专辑队列

    private ArrayList<Song> songList;

    private String artistName;

    private RecyclerView albumListView;
    private RecyclerView songListView;

    private MedicalApp medicalApp;

    private ArtistAlbumListAdapter mAlbumListAdapter;
    private ArtistSongListAdapter mDetailSongListAdapter;

    @Override
    protected void handleMessageCallback(Message msg) {
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

    @Override
    protected void initISongManager(ISongManager mISongManager) {

    }

    @Override
    protected void bottomIsVisibility(boolean isVisibility, int height) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_song);
        setBottomViewVisibility(true);
        initBottomView();

        medicalApp = (MedicalApp)getApplication();
        Intent intent = getIntent();
        songList = intent.getParcelableArrayListExtra(Constant.SONG_LIST);
        artistName = intent.getStringExtra(LoadingMusicTask.artistName);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(artistName);
        setSupportActionBar(toolbar);

//        View heard = LayoutInflater.from(this).inflate(R.layout.artist_album_list_heard,null);
        albumListView = (RecyclerView)findViewById(R.id.artist_album_list) ;
        albumListView.setLayoutManager(new StaggeredGridLayoutManager(1
                , OrientationHelper.HORIZONTAL));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DividerGridItemDecoration artistDecoration = new DividerGridItemDecoration(16);
            albumListView.addItemDecoration(artistDecoration);
        }
        mAlbumListAdapter = new ArtistAlbumListAdapter();
        albumListView.setAdapter(mAlbumListAdapter);
        mAlbumListAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                HashMap<String, String> album = (HashMap<String, String>) data;
                Intent intent = new Intent(ArtistSongActivity.this, AlbumSongActivity.class);
                intent.putExtra(LoadingMusicTask.albumId
                        , album.get(LoadingMusicTask.albumId));
                intent.putExtra(LoadingMusicTask.albumName
                        , album.get(LoadingMusicTask.albumName));
                intent.putExtra(LoadingMusicTask.artistName
                        , album.get(LoadingMusicTask.artistName));
                intent.putExtra(LoadingMusicTask.songNumber
                        , album.get(LoadingMusicTask.songNumber));
                if (mDetailSongListAdapter != null) {
                    ArrayList<Song> songList = mDetailSongListAdapter.getData();
                    intent.putExtra(AlbumFragment.SONG_LIST, songList);
                }
                startActivity(intent);
            }
        });

        songListView = (RecyclerView)findViewById(R.id.artist_song_list);
        songListView.setLayoutManager(new LinearLayoutManager(this));
        mDetailSongListAdapter = new ArtistSongListAdapter();
        songListView.setAdapter(mDetailSongListAdapter);
        mDetailSongListAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                Intent intent = new Intent(ArtistSongActivity.this, MusicPlayerActivity.class);
                intent.putExtra(Constant.PLAYING_POSITION,position);
                intent.putExtra(Constant.PLAYING_LIST
                        ,(ArrayList<Song>) mDetailSongListAdapter.getData());
                startActivity(intent);
            }
        });
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(songListView);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                //这里是bottomSheet 状态的改变回调
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //这里是拖拽中的回调，根据slideOffset可以做一些动画
            }
        });


        medicalApp.execute(new Runnable() {
            @Override
            public void run() {
                initArtistDetailList(songList,artistName);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
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
