package com.lr.musiceasynet;

import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;

import com.lr.musiceasynet.util.CommonUtil;

public class NightModeAdapter {

    public static Drawable imgPlay(){
        if (CommonUtil.isDarkMode(MyApplication.getContext())) {
            return AppCompatResources.getDrawable(MyApplication.getContext(),R.drawable.play_dark);
        }
        return AppCompatResources.getDrawable(MyApplication.getContext(),R.drawable.play_light);
    }

    public static Drawable imgPause(){
        if (CommonUtil.isDarkMode(MyApplication.getContext())) {
            return AppCompatResources.getDrawable(MyApplication.getContext(),R.drawable.pause_dark);
        }
        return AppCompatResources.getDrawable(MyApplication.getContext(),R.drawable.pause_light);
    }

    public static Drawable imgTune(){
        if (CommonUtil.isDarkMode(MyApplication.getContext())) {
            return AppCompatResources.getDrawable(MyApplication.getContext(), R.drawable.tune_dark);
        }
        return AppCompatResources.getDrawable(MyApplication.getContext(), R.drawable.tune_light);
    }

}
