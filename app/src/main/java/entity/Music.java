package entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
*@author By Dobby Tang
*Created on 2016-03-11 16:52
*/
public class Music implements Parcelable {
    protected Music(Parcel in) {
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
