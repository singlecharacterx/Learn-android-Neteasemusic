package com.lr.musiceasynet;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.provider.MediaStore;

import java.io.IOException;
import java.util.Objects;

public class MusicInfo {
    public MusicInfo(){

    }
    public MusicInfo(String url, long id, String title, String artist, long duration,long album) {
        this.url = url;
        this.id = id;
        this.title = title;
        this.artisst = artist;
        this.duration = duration;
        this.album = album;
    }

    public MusicInfo(Cursor cursor) {
        setUrl(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
        setId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
        setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
        setArtisst(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
        setDuration(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
        setAlbum(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
    }

    private String url;
    private String title;
    private String artisst;
    private long duration;
    private long id;
    private long album;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtisst() {
        return artisst;
    }

    public void setArtisst(String artisst) {
        this.artisst = artisst;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAlbum() {
        return album;
    }

    public void setAlbum(long album) {
        this.album = album;
    }

    public static String formatTime(long time){
        if (time / 1000 % 60 < 10) {
            return (time / 1000 / 60) + ":0" + time / 1000 % 60;
        } else {
            return (time / 1000 / 60) + ":" + time / 1000 % 60;
        }
    }

    public Bitmap getMusicImg(){
        Bitmap bitmap = null;
        if (getUrl()!=null&& Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
            try (MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever()) {
                mediaMetadataRetriever.setDataSource(getUrl());
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
