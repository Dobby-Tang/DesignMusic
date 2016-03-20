// IMusicManager.aidl
package com.example.android.designmusic;

import com.example.android.designmusic.entity.Song;
import com.example.android.designmusic.IAudioStatusChangeListener;

interface ISongManager {
     List<Song> getSongList();
     void initSongList(in List<Song> songList);
     void addSong(in Song song);
     void play(in int songPosition);
     void stop();
     void pause();

     boolean isPlaying();

     void registerCallBack(IAudioStatusChangeListener mListener);
     void unregisterCallBack(IAudioStatusChangeListener mListener);
}
