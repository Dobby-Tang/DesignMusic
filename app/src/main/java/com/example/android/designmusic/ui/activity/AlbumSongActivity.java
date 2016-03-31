package com.example.android.designmusic.ui.activity;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.example.android.designmusic.ui.adapter.BaseListAdapter;
import com.example.android.designmusic.ui.adapter.DetailSongListAdapter;
import com.example.android.designmusic.ui.fragment.HomeFragment;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class AlbumSongActivity extends AppCompatActivity {

    private static final int ALBUM_SONG_LIST = 1;

    private String albumName;
    private String albumId;
    private String artistName;
    private String songNumber;

    private ArrayList<Song> SongList;

    private RecyclerView mDetailList;
    private SimpleDraweeView albumCover;
    private TextView artistNameTextView;
    private TextView songNumberTextView;

    private DetailSongListAdapter mDetailSongListAdapter;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ALBUM_SONG_LIST:
                    ArrayList<Song> albumSongList = (ArrayList<Song>) msg.obj;
                    if (mDetailSongListAdapter != null){
                        mDetailSongListAdapter.setDatas(albumSongList);
                        mDetailSongListAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_song);

        albumCover = (SimpleDraweeView)findViewById(R.id.album_cover);
        mDetailList = (RecyclerView)findViewById(R.id.detail_list);
        artistNameTextView = (TextView)findViewById(R.id.detail_artist_name);
        songNumberTextView = (TextView)findViewById(R.id.detail_song_num);

        SongList = getIntent().getParcelableArrayListExtra(HomeFragment.SONG_LIST);
        albumName = getIntent().getStringExtra(LoadingMusicTask.albumName);
        albumId = getIntent().getStringExtra(LoadingMusicTask.albumId);
        artistName = getIntent().getStringExtra(LoadingMusicTask.artistName);
        songNumber = getIntent().getStringExtra(LoadingMusicTask.songNumber);


        artistNameTextView.setText(artistName);
        songNumberTextView.setText("曲目: " + songNumber);
        Uri uri = ContentUris.withAppendedId(LoadingMusicTask.albumArtUri
                ,Integer.valueOf(albumId));
        albumCover.setImageURI(uri);

        mDetailSongListAdapter = new DetailSongListAdapter();
        mDetailSongListAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                Intent intent = new Intent(AlbumSongActivity.this, MusicPlayerActivity.class);
                intent.putExtra(HomeFragment.PLAYIONG_POSITION,position);
                intent.putExtra(HomeFragment.PLAYIONG_LIST
                        ,(ArrayList<Song>) mDetailSongListAdapter.getData());
                startActivity(intent);
            }
        });
        mDetailList.setLayoutManager(new LinearLayoutManager(this));
        mDetailList.setAdapter(mDetailSongListAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                initAlbumSongList(SongList,albumId);
            }
        }).start();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(albumName);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    public void initAlbumSongList(ArrayList<Song> songList, String albumId){
        ArrayList<Song> albumSongList = new ArrayList<>();
        if (songList != null){
            for (int i = 0; i< songList.size();i++){
                if (songList.get(i).song.get(LoadingMusicTask.albumId).equals(albumId)){
                    albumSongList.add(songList.get(i));
                }
            }
        }

        Message msg = new Message();
        msg.what = ALBUM_SONG_LIST;
        msg.obj = albumSongList;
        mHandler.sendMessage(msg);

    }


}
