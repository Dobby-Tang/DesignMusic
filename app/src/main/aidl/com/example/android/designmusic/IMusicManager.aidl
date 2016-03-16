// IMusicManager.aidl
package com.example.android.designmusic;

import com.example.android.designmusic.entity.Song;

interface IMusicManager {
     List<Song> getSongList();
     void addMusic(in Song song);
     void player(in int position);
     void stop();
     void pause();
}
