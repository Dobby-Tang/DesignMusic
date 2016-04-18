package com.example.android.designmusic.ui.fragment;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.designmusic.IAudioStatusChangeListener;
import com.example.android.designmusic.IRefreshCurrentTimeListener;
import com.example.android.designmusic.ISongManager;
import com.example.android.designmusic.MainActivity;
import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.player.service.MusicService;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.example.android.designmusic.utils.FormatTime;
import com.example.android.designmusic.player.Constant;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wnafee.vector.MorphButton;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;

/**
*@author By Dobby Tang
*Created on 2016-03-15 13:51
*/

public class MusicPlayerFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "MusicPlayerFragment";



    private MorphButton playerBtn;
    private SimpleDraweeView musicCover;
    private ImageView nextBtn;
    private ImageView lastBtn;
    private DiscreteSeekBar mDiscreteSeekBar;
    private TextView currentTime ;
    private TextView totalTime;

    private boolean mDiscreteSeekBarIsStart = false;


    playingCallback playingCallbackListener;

    int position;
    private static ArrayList<Song> mPlayingList;

    public ISongManager mISongManager = null;

    private ServiceConnection songPlayerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mISongManager = ISongManager.Stub.asInterface(service);
            try {
                mISongManager.registerAudioCallBack(mAudioListener);
                mISongManager.registerCurrentTimeCallBack(mRefreshListener);
                mISongManager.initSongList(mPlayingList);
                playingCallbackListener.getISongManager(mISongManager);
                Message msg = Message.obtain();
                msg.arg1 = mISongManager.getSongPosition();
                msg.what = Constant.PLAYING_CALL_BACK;
                mHandler.sendMessage(msg);
                Log.d(TAG,"select positon is " + position + " get service now playing position = "
                        + mISongManager.getSongPosition());
                mISongManager.play(mISongManager.setSongPosition(position));

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mISongManager = null;
        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.AUDIO_PLAYING_CALL_BACK:
                    playerBtn.setState(MorphButton.MorphState.START);
                    break;

                case Constant.AUDIO_PAUSE_CALL_BACK:
                    playerBtn.setState(MorphButton.MorphState.END);
                    break;

                case Constant.PLAYING_CALL_BACK:
                    Song song = (Song)msg.obj;
                    if (song != null){
                        initSongData(song);
                    }
                    position = msg.arg2;
                    playingCallbackListener.onSongPosition(position);
                    break;

                case Constant.PLAYING_TIME_CALL_BACK:
                    if (!mDiscreteSeekBarIsStart){
                        mDiscreteSeekBar.setProgress(msg.arg1);
                    }
                    currentTime.setText(FormatTime.secToTime(msg.arg1));
                    break;
            }
        }
    };



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            playingCallbackListener = (playingCallback) context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }

    }

    public static MusicPlayerFragment newInstance(ArrayList<Song> mPlayingList, int position) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(MainActivity.PLAYIONG_LIST,mPlayingList);
        args.putInt(MainActivity.PLAYIONG_POSITION,position);
        MusicPlayerFragment fragment = new MusicPlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mPlayingList = getArguments().getParcelableArrayList(MainActivity.PLAYIONG_LIST);
            position = getArguments().getInt(MainActivity.PLAYIONG_POSITION,0);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_player,container,false);
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent,songPlayerServiceConnection, Context.BIND_AUTO_CREATE);

        musicCover = (SimpleDraweeView) view.findViewById(R.id.music_player_cover);
        playerBtn = (MorphButton) view.findViewById(R.id.music_player_playBtn);
        nextBtn = (ImageView) view .findViewById(R.id.music_player_nextBtn);
        lastBtn = (ImageView) view.findViewById(R.id.music_player_lastBtn);
        mDiscreteSeekBar = (DiscreteSeekBar) view.findViewById(R.id.music_player_discrete);
        totalTime = (TextView) view.findViewById(R.id.music_player_total_playing_time);
        currentTime = (TextView) view.findViewById(R.id.music_player_current_playing_time);

        nextBtn.setOnClickListener(this);
        lastBtn.setOnClickListener(this);

        playerBtn.setOnStateChangedListener(new MorphButton.OnStateChangedListener() {
            @Override
            public void onStateChanged(MorphButton.MorphState changedTo, boolean isAnimating) {
                try {
                    switch (changedTo){
                        case START:
                            position = mISongManager.getSongPosition();
                            mISongManager.play(position);
                            break;
                        case END:
                            mISongManager.pause();
                            break;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });

        initDiscreteSeekBar();

        return view;
    }


    /**
    *service回调监听器
    *@author Dobby-Tang
    *created at 16/3/20  下午12:31
    */
    private IAudioStatusChangeListener mAudioListener = new IAudioStatusChangeListener.Stub() {
        @Override
        public void AudioIsStop() throws RemoteException {
            Log.d(TAG,"Audio is stop");
            getActivity().finish();
        }

        @Override
        public void AudioIsPause() throws RemoteException {
            Log.d(TAG,"Audio is pause");
            Message msg = Message.obtain();
            msg.what = Constant.AUDIO_PAUSE_CALL_BACK;
            mHandler.sendMessage(msg);
        }

        @Override
        public void AudioIsPlaying() throws RemoteException {
            Log.d(TAG,"Audio is playing");
            Message msg = Message.obtain();
            msg.what = Constant.AUDIO_PLAYING_CALL_BACK;
            mHandler.sendMessage(msg);
        }


        @Override
        public void playingCallback(int position,Song song) throws RemoteException {
            Log.d(TAG, "playingCallback: " + "play song is: "+ position);
            Message msg = Message.obtain();
            msg.what = Constant.PLAYING_CALL_BACK;
            msg.arg2 = position;
            msg.obj = song;
            mHandler.sendMessage(msg);
            ArrayList<Song> songList = new ArrayList<>();
            songList.addAll(mISongManager.getSongList());
            playingCallbackListener.getSongList(songList);
        }
    };

    private IRefreshCurrentTimeListener mRefreshListener = new IRefreshCurrentTimeListener.Stub(){

        @Override
        public void playingCurrentTimeCallback(int time) throws RemoteException {
            Message msg = new Message();
            msg.what = Constant.PLAYING_TIME_CALL_BACK;
            msg.arg1 = time / 1000;
            mHandler.sendMessage(msg);
        }
    };

    private void initSongData(Song mSong){
        int albumId = Integer.parseInt(mSong.song.get(LoadingMusicTask.albumId));
        Uri uri = ContentUris.withAppendedId(LoadingMusicTask.albumArtUri,albumId);
        musicCover.setImageURI(uri);
        totalTime.setText(mSong.song.get(LoadingMusicTask.duration));
        mDiscreteSeekBar.setProgress(0);
        mDiscreteSeekBar.setMax(Integer
                .valueOf(mSong.song.get(LoadingMusicTask.duration_t)) / 1000);

        if (playerBtn.getState() == MorphButton.MorphState.END){
            playerBtn.setState(MorphButton.MorphState.START);
        }
    }

    private void initDiscreteSeekBar(){
        mDiscreteSeekBar.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                return 0;
            }

            @Override
            public String transformToString(int value) {
                return FormatTime.secToTime(value);
            }

            @Override
            public boolean useStringTransform() {
                return true;
            }
        });

        mDiscreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            int value;
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                this.value = value;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
                mDiscreteSeekBarIsStart = true;

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                try {
                    mISongManager.seekTo(value * 1000);
                    mDiscreteSeekBarIsStart = false;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mISongManager != null){
            try {
                mISongManager.registerCurrentTimeCallBack(mRefreshListener);
                if (mISongManager.isPlaying()){
                    playerBtn.setState(MorphButton.MorphState.START);
                }else {
                    playerBtn.setState(MorphButton.MorphState.END);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            mISongManager.unregisterCurrentTimeCallBack(mRefreshListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mISongManager.unregisterAudioCallBack(mAudioListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        getActivity().unbindService(songPlayerServiceConnection);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.music_player_nextBtn:
                try {
                    mISongManager.next();
                } catch (RemoteException e) {

                }
                break;

            case R.id.music_player_lastBtn:
                try {
                    mISongManager.last();
                }catch (RemoteException e){
                    e.printStackTrace();
                }
                break;
        }
    }


    /**
     * 回调通知Activity
    *@author By Dobby Tang
    *Created on 2016-03-29 14:16
    */
    public interface playingCallback{
        void onSongPosition(int position);
        void getSongList(ArrayList<Song> SongList);
        void getISongManager(ISongManager mISongManager);
    }
}
