package com.example.android.designmusic.ui.activity;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.android.designmusic.R;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.facebook.drawee.view.SimpleDraweeView;

public class AlbumSongActivity extends AppCompatActivity {

    private String albumName;
    private int albumId;

    private SimpleDraweeView albumCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_song);

        albumCover = (SimpleDraweeView)findViewById(R.id.album_cover);

        albumName = getIntent().getStringExtra(LoadingMusicTask.albumName);
        albumId = Integer.valueOf(getIntent().getStringExtra(LoadingMusicTask.albumId));
        Uri uri = ContentUris.withAppendedId(LoadingMusicTask.albumArtUri,albumId);
        albumCover.setImageURI(uri);

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

}
