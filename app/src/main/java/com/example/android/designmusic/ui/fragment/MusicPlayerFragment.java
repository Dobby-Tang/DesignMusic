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
import com.example.android.designmusic.ISongManager;
import com.example.android.designmusic.R;
import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.player.service.MusicService;
import com.example.android.designmusic.task.LoadingMusicTask;
import com.example.android.designmusic.utils.FormatTime;
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

    private static final int AUDIO_PAUSE_CALL_BACK = 0;         //音频焦点暂停播放
    private static final int AUDIO_PLAYING_CALL_BACK = 1;       //音频焦点开始播放
    private static final int PLAYING_CALL_BACK = 2;             //service开始播放回调
    private static final int PLAYING_TIME_CALL_BACK = 4;        //播放进度回调

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

    static MorphButton.MorphState START = MorphButton.MorphState.START;
    static MorphButton.MorphState END = MorphButton.MorphState.END;

    public ISongManager mISongManager = null;

    private ServiceConnection songPlayerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mISongManager = ISongManager.Stub.asInterface(service);
            try {
                mISongManager.registerCallBack(mListener);
                mISongManager.initSongList(mPlayingList);
                playingCallbackListener.getISongManager(mISongManager);
                Message msg = Message.obtain();
                msg.arg1 = mISongManager.getSongItem();
                msg.what = PLAYING_CALL_BACK;
                mHandler.sendMessage(msg);
                Log.d(TAG,"select positon is " + position + " get service now playing position = "
                        + mISongManager.getSongItem());
                mISongManager.play(position);
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
                case AUDIO_PLAYING_CALL_BACK:
                    playerBtn.setState(START);
                    Log.d(TAG, "setState START");
                    break;

                case AUDIO_PAUSE_CALL_BACK:
                    playerBtn.setState(END);
                    Log.d(TAG, "setState END");
                    break;

                case PLAYING_CALL_BACK:
                    initSongData(msg.arg1);
                    playingCallbackListener.onSongPosition(msg.arg1);
                    Log.d(TAG, "handleMessage: play callback");
                    break;

                case PLAYING_TIME_CALL_BACK:
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
        args.putParcelableArrayList(HomeFragment.PLAYIONG_LIST,mPlayingList);
        args.putInt(HomeFragment.PLAYIONG_POSITION,position);
        MusicPlayerFragment fragment = new MusicPlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mPlayingList = getArguments().getParcelableArrayList(HomeFragment.PLAYIONG_LIST);
            position = getArguments().getInt(HomeFragment.PLAYIONG_POSITION,0);

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
                switch (changedTo){
                    case START:
                        try {
                            position = mISongManager.getSongItem();
                            mISongManager.play(position);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    case END:
                        try {
                            mISongManager.pause();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
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
    private IAudioStatusChangeListener mListener = new IAudioStatusChangeListener.Stub() {
        @Override
        public void AudioIsStop() throws RemoteException {
            Log.d(TAG,"Audio is stop");
            getActivity().finish();
        }

        @Override
        public void AudioIsPause() throws RemoteException {
            Log.d(TAG,"Audio is pause");
            Message msg = Message.obtain();
            msg.what = AUDIO_PAUSE_CALL_BACK;
            mHandler.sendMessage(msg);
        }

        @Override
        public void AudioIsPlaying() throws RemoteException {
            Log.d(TAG,"Audio is playing");
            Message msg = Message.obtain();
            msg.what = AUDIO_PLAYING_CALL_BACK;
            mHandler.sendMessage(msg);
        }


        @Override
        public void playingCallback(int position) throws RemoteException {
            Log.d(TAG, "playingCallback: " + "play song is: "+ position);
            Message msg = Message.obtain();
            msg.arg1 = position;
            msg.what = PLAYING_CALL_BACK;
            mHandler.sendMessage(msg);
            ArrayList<Song> songList = new ArrayList<>();
            songList.addAll(mISongManager.getSongList());
            playingCallbackListener.getSongList(songList);
        }

        @Override
        public void playingCurrentTimeCallback(int time) throws RemoteException {
//            Log.d(TAG, "playingCurrentTimeCallback: " + mFormatTime.secToTime(time/1000));
            Message msg = new Message();
            msg.what = PLAYING_TIME_CALL_BACK;
            msg.arg1 = time / 1000;
            mHandler.sendMessage(msg);
        }

    };

    private void initSongData(int Position){
        if (Position >= 0){
            Song mSong = mPlayingList.get(Position);
            int albumId = Integer.parseInt(mSong.song.get(LoadingMusicTask.albumId));
            Uri uri = ContentUris.withAppendedId(LoadingMusicTask.albumArtUri,albumId);
            musicCover.setImageURI(uri);
            totalTime.setText(mSong.song.get(LoadingMusicTask.duration));
            mDiscreteSeekBar.setProgress(0);
            mDiscreteSeekBar.setMax(Integer
                    .valueOf(mSong.song.get(LoadingMusicTask.duration_t)) / 1000);

            if (playerBtn.getState() == END){
                playerBtn.setState(START);
            }
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
                if (mISongManager.isPlaying()){
                    playerBtn.setState(START);
                }else {
                    playerBtn.setState(END);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mISongManager.unregisterCallBack(mListener);
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
