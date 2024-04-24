package com.lr.musiceasynet.music;

import android.database.Cursor;
import android.provider.MediaStore;

import com.lr.musiceasynet.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class MusicInfosProvider {

    public static List<MusicInfo> getMusicInfos(){
        Cursor cursor = MyApplication.getContext().getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,
                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER,null);
        List<MusicInfo> musicInfos = new ArrayList<>();
        assert cursor!=null; //cursor永远非空
        while (cursor.moveToNext()){
            MusicInfo musicInfo = new MusicInfo(cursor);
            musicInfos.add(musicInfo);
        }
        cursor.close();
        return musicInfos;
    }

}
