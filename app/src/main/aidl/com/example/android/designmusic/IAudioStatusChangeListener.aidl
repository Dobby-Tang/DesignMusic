// IAudioStatusChangeListener.aidl
package com.example.android.designmusic;

// Declare any non-default types here with import statements

interface IAudioStatusChangeListener {
     void AudioIsStop();
     void AudioIsPause();
     void AudioIsPlaying();

     void playingCallback(in int position);
}
