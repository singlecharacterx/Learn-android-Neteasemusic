package com.lr.musiceasynet.util;

import android.content.Context;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CommonUtil {

    private final static int DARKMODE_CODE = 0x21;

    private CommonUtil(){}

    public static boolean isDarkMode(Context context){

        return context.getResources().getConfiguration().uiMode == DARKMODE_CODE;
    }

    public static void edgeToEdge(ComponentActivity componentActivity, View root){
        EdgeToEdge.enable(componentActivity);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

}
