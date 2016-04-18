package com.example.android.designmusic;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.designmusic.ui.activity.BaseActivity;
import com.example.android.designmusic.ui.adapter.HomeFragmentPagerAdapter;
import com.example.android.designmusic.ui.fragment.AlbumFragment;
import com.example.android.designmusic.ui.fragment.ArtistFragment;
import com.example.android.designmusic.ui.fragment.SongFragment;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wnafee.vector.MorphButton;


/**
*@author By Dobby Tang
*Created on 2016-03-04 15:36
*/
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    public static final String PLAYIONG_LIST = "Playing_list";             //所有本地歌曲
    public static final String PLAYIONG_POSITION = "Playing_position";     //播放歌曲序号
    public static final String SONG_LIST = "album_song_list" ;        //专辑歌曲列表

    private DrawerLayout mDrawerLayout;
    private Resources resources;

    private TextView songName;
    private TextView artistName;
    private MorphButton play;
    private SimpleDraweeView albumCover;
    private LinearLayout playingBottomView;
    ViewPager viewPager;

//    private ISongManager mISongManager;
//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            mISongManager = ISongManager.Stub.asInterface(service);
//            try {
//                mISongManager.registerAudioCallBack(mlistener);
//                if (mISongManager.isPlaying()){
//                    play.setState(MorphButton.MorphState.START);
//                }else {
//                    play.setState(MorphButton.MorphState.END);
//                }
//
//                if (mISongManager.getSongPosition() < 0){
//                    playingBottomView.setVisibility(View.GONE);
//                }else {
//                    updateBottomPlayView(mISongManager.getSongItem());
//                }
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    };


//    private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case Constant.PLAYING_CALL_BACK:
//                    Song song = (Song)msg.obj;
//                    try {
//                        updateBottomPlayView(song);
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                case Constant.IS_PLAYING:
//                    play.setState(MorphButton.MorphState.START);
//                    break;
//                case Constant.IS_UN_PLAYING:
//                    play.setState(MorphButton.MorphState.END);
//                    break;
//            }
//
//        }
//    };


//    private IAudioStatusChangeListener mlistener = new IAudioStatusChangeListener.Stub() {
//        @Override
//        public void AudioIsStop() throws RemoteException {
//            Message msg = Message.obtain();
//            msg.what = Constant.IS_UN_PLAYING;
//            mHandler.sendMessage(msg);
//        }
//
//        @Override
//        public void AudioIsPause() throws RemoteException {
//            Message msg = Message.obtain();
//            msg.what = Constant.IS_UN_PLAYING;
//            mHandler.sendMessage(msg);
//        }
//
//        @Override
//        public void AudioIsPlaying() throws RemoteException {
//            Message msg = Message.obtain();
//            msg.what = Constant.IS_PLAYING;
//            mHandler.sendMessage(msg);
//        }
//
//        @Override
//        public void playingCallback(int position,Song song) throws RemoteException {
//            Log.d(TAG, "playingCallback: "+ song.song.get(LoadingMusicTask.songName));
//            Message msg = Message.obtain();
//            msg.what = Constant.PLAYING_CALL_BACK;
//            msg.obj = song;
//            mHandler.sendMessage(msg);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fresco.initialize(MainActivity.this);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        setBottomViewVisibility(true);
        initBottomView();

        resources = getResources();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.main_nav_view);
        if(navigationView != null){
            setupDrawerContent(navigationView);
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if(viewPager != null){
            setupViewPager(viewPager);
        }

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

//        Intent intent = new Intent(this, MusicService.class);
//        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);

//        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (mISongManager != null){
//            try {
//                if (mISongManager.isPlaying()){
//                    play.setState(MorphButton.MorphState.START);
//                }else {
//                    play.setState(MorphButton.MorphState.END);
//                }
//
//                if (mISongManager.getSongPosition() < 0){
//                    playingBottomView.setVisibility(View.GONE);
//                }else {
//                    updateBottomPlayView(mISongManager.getSongItem());
//                }
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }else{
//            playingBottomView.setVisibility(View.GONE);
//        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mISongManager != null){
//            try {
//                mISongManager.unregisterAudioCallBack(mlistener);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        unbindService(serviceConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    private void initView(){
//        songName = (TextView)findViewById(R.id.home_music_name);
//        artistName = (TextView)findViewById(R.id.home_artist_name);
//        play = (MorphButton)findViewById(R.id.home_music_playBtn);
//        albumCover = (SimpleDraweeView)findViewById(R.id.home_music_album_img);
//        playingBottomView = (LinearLayout)findViewById(R.id.playing_bottom_view);
//
//        playingBottomView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    Intent intent = new Intent(MainActivity.this, MusicPlayerActivity.class);
//                    intent.putExtra(PLAYIONG_POSITION,mISongManager.getSongPosition());
//                    intent.putExtra(PLAYIONG_LIST
//                            ,(ArrayList<Song>) mISongManager.getSongList());
//                    startActivity(intent);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        play.setOnStateChangedListener(new MorphButton.OnStateChangedListener() {
//            @Override
//            public void onStateChanged(MorphButton.MorphState changedTo, boolean isAnimating) {
//                switch (changedTo){
//                    case START:
//                        try {
//                            int position = -1;
//                            position = mISongManager.getSongPosition();
//                            if (position >= 0){
//                                mISongManager.play(position);
//                            }
//                        } catch (RemoteException e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                    case END:
//                        try {
//                            mISongManager.pause();
//                        } catch (RemoteException e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                }
//
//            }
//        });

//    }

    private void setupViewPager(ViewPager viewPager){
        HomeFragmentPagerAdapter homeFragmentPagerAdapter =
                new HomeFragmentPagerAdapter(getSupportFragmentManager());

        SongFragment songFragment = SongFragment.newInstance(SongFragment.TYPE_SONG);
        homeFragmentPagerAdapter.addFragment(
                songFragment,resources.getString(R.string.local_song));

        ArtistFragment artistFragment = ArtistFragment.newInstance(ArtistFragment.TYPE_ARTIST);
        homeFragmentPagerAdapter.addFragment(
                artistFragment,resources.getString(R.string.local_artist));

        AlbumFragment albumFragment = AlbumFragment.newInstance(AlbumFragment.TYPE_ALBUM);
        homeFragmentPagerAdapter.addFragment(
                albumFragment,resources.getString(R.string.local_album));

        viewPager.setAdapter(homeFragmentPagerAdapter);
    }

//    private void updateBottomPlayView(Song song) throws RemoteException {
//        if (song != null){
//            int padding_in_dp = 64;  // 6 dps
//            final float scale = getResources().getDisplayMetrics().density;
//            int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
//            viewPager.setPadding(0,0,0,padding_in_px);
//            playingBottomView.setVisibility(View.VISIBLE);
//            songName.setText(song.song.get(LoadingMusicTask.songName));
//            artistName.setText(song.song.get(LoadingMusicTask.artistName));
//            Uri uri = ContentUris.withAppendedId(LoadingMusicTask.albumArtUri
//                    ,Integer.valueOf(song.song.get(LoadingMusicTask.albumId)));
//            albumCover.setImageURI(uri);
//        }
//    }

    @Override
    protected void handleMessageCallback(Message msg) {

    }

    @Override
    protected void initISongManager(ISongManager mISongManager) {

    }

    @Override
    protected void bottomIsVisibility(boolean isVisibility, int height) {
        if (isVisibility){
            viewPager.setPadding(0,0,0,height);
        }else{
            viewPager.setPadding(0,0,0,0);
        }
    }


    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        if(item.getItemId() != R.id.sub_1 && item.getItemId() != R.id.sub_2){
                        item.setChecked(true);
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                }

        );
    }
}
