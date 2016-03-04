package com.example.android.designmusic.model;

import android.support.v4.media.MediaMetadataCompat;

import java.util.Iterator;

/**
 * Created by KJHmobileAdmin on 2016-03-04.
 */
public interface MusicProviderSource {
    String CUSTOM_METADATA_TRACK_SOURCE = "_SOURCE_";
    Iterator<MediaMetadataCompat> iterator();   //迭代器
}
