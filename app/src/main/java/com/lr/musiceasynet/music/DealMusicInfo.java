package com.lr.musiceasynet.music;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.provider.MediaStore;

import com.lr.musiceasynet.MusicInfo;
import com.lr.musiceasynet.MyApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DealMusicInfo {
    private static final String TAG = "DealMusicInfo";

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

    public static Bitmap getMusicImg(MusicInfo musicInfo){
        Bitmap bitmap = null;
        if (musicInfo.getUrl()!=null&& Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
            try (MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever()) {
                mediaMetadataRetriever.setDataSource(musicInfo.getUrl());
                if (mediaMetadataRetriever.getEmbeddedPicture()!=null)
                    bitmap = BitmapFactory.decodeByteArray(mediaMetadataRetriever.getEmbeddedPicture(),
                            0, Objects.requireNonNull(mediaMetadataRetriever.getEmbeddedPicture()).length);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }//版本适配待做

        return bitmap;
    }
}
