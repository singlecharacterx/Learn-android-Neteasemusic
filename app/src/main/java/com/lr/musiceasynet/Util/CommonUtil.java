package com.lr.musiceasynet.Util;

import com.lr.musiceasynet.MyApplication;

public class CommonUtil {

    public static boolean isDarkMode(){
        return MyApplication.getContext().getResources().getConfiguration().uiMode == 0x21;
    }

}
