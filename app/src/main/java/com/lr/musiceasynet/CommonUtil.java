package com.lr.musiceasynet;

public class CommonUtil {

    public static boolean isDarkMode(){
        return MyApplication.getContext().getResources().getConfiguration().uiMode == 0x21;
    }

}
