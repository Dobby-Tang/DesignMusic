// IMusicManager.aidl
package com.example.android.designmusic;

import com.example.android.designmusic.player.entity.Music;

interface IMusicManager {
     List<Music> getMusicList();
     void addMusic(in Music music);
     void player(in int position);
     void stop();
     void pause();
}
