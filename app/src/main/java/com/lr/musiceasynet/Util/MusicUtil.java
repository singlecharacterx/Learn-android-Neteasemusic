package com.lr.musiceasynet.Util;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import com.lr.musiceasynet.MusicInfo;
import com.lr.musiceasynet.MyApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MusicUtil {
    private static final String TAG = "MusicUtil";

    public static List<MusicInfo> getMusicInfos(){
        Cursor cursor = MyApplication.getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER,null);
        List<MusicInfo> musicInfos = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()){
                MusicInfo musicInfo = new MusicInfo(cursor);
                musicInfos.add(musicInfo);
            }
            cursor.close();
        }

        return musicInfos;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Bitmap getMusicImg(MusicInfo musicInfo){
        Bitmap bitmap = null;
        if (musicInfo.getUrl()!=null) {
            try (MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever()) {
                mediaMetadataRetriever.setDataSource(musicInfo.getUrl());
                if (mediaMetadataRetriever.getEmbeddedPicture()!=null) bitmap = BitmapFactory.decodeByteArray(mediaMetadataRetriever.getEmbeddedPicture(), 0, Objects.requireNonNull(mediaMetadataRetriever.getEmbeddedPicture()).length);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return bitmap;
    }
}
