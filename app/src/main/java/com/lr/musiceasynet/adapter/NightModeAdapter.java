package com.lr.musiceasynet.adapter;

import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;

import com.lr.musiceasynet.MyApplication;
import com.lr.musiceasynet.util.CommonUtil;

public class NightModeAdapter {

    public static Drawable getImg(int imgDark,int imgLight){
        if (CommonUtil.isDarkMode(MyApplication.getContext())) {
            return AppCompatResources.getDrawable(MyApplication.getContext(),imgDark);
        }
        return AppCompatResources.getDrawable(MyApplication.getContext(),imgLight);
    }

}
