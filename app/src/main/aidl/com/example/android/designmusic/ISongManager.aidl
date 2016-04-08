// IMusicManager.aidl
package com.example.android.designmusic;

import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.IAudioStatusChangeListener;
import com.example.android.designmusic.IRefreshCurrentTimeListener;

interface ISongManager {
     List<Song> getSongList();
     void initSongList(in List<Song> songList);
     void addSong(in Song song);
     int getSongItem();
     int getPlayingMode();
     void setPlayingMode(int mode);

     void play(in int songPosition,in boolean isListClick);
     void stop();
     void pause();
     void next();
     void last();

     void seekTo(in int progress);

     boolean isPlaying();
     boolean isEqualsSongList(in List<Song> songList);

     void registerAudioCallBack(IAudioStatusChangeListener mListener);
     void unregisterAudioCallBack(IAudioStatusChangeListener mListener);

     void registerCurrentTimeCallBack(IRefreshCurrentTimeListener mListener);
     void unregisterCurrentTimeCallBack(IRefreshCurrentTimeListener mListener);
}
