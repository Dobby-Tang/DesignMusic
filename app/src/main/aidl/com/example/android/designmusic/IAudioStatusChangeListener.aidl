// IAudioStatusChangeListener.aidl
package com.example.android.designmusic;
import com.example.android.designmusic.entity.Song;
// Declare any non-default types here with import statements

interface IAudioStatusChangeListener {
     void AudioIsStop();
     void AudioIsPause();
     void AudioIsPlaying();

     void playingCallback(in int position,in Song song);
}
