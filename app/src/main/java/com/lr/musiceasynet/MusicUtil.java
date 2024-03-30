package com.lr.musiceasynet;

import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class MusicUtil {
    private static final String TAG = "MusicUtil";

    public static List<MusicInfo> getMusicInfos(){
        Cursor cursor = MyApplication.getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER,null);
        List<MusicInfo> musicInfos = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()){
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                musicInfo.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                musicInfo.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                musicInfo.setArtisst(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                musicInfo.setDuration(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                musicInfo.setAlbum(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
                musicInfos.add(musicInfo);
            }
            cursor.close();
        }

        return musicInfos;
    }
}
