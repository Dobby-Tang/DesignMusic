package com.example.android.designmusic.utils;

/**
*@author By Dobby Tang
*Created on 2016-03-29 14:02
*/
public class FormatTime {

    public static String secToTime(int time){
        int minute = 0;
        int second = 0;
        String timeStr = null ;
        if (time <= 0){
            return "00:00";
        }else {
            minute = time / 60;
            if (minute < 60){
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }
}
