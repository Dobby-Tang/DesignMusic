package com.example.android.designmusic.utils;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
*@author By Dobby Tang
*Created on 2016-04-05 14:04
*/
public class DrawableHelper {

    public static Drawable TintDrawable(Drawable drawable, ColorStateList colors){
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable,colors);
        return wrappedDrawable;
    }

    public static void getMainColor( Bitmap icon){

    }

}
