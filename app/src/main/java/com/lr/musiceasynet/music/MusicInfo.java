package com.lr.musiceasynet.music;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.provider.MediaStore;

public class MusicInfo {

    public final static int MILLS_IN_SECOND = 1000;
    public final static int MILLS_IN_MINUTE = 60;

    private final static int INT_SINGLE_DIGIT = 10;

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

    public String formatTime(){
        if (calculateDurationSec() < INT_SINGLE_DIGIT) {
            return calculateDurationMin() + ":0" + calculateDurationSec();
        } else {
            return calculateDurationMin() + ":" + calculateDurationSec();
        }
    }

    private long calculateDurationMin(){
        return (this.duration / MILLS_IN_SECOND / MILLS_IN_MINUTE);
    }

    private long calculateDurationSec(){
        return (this.duration / MILLS_IN_SECOND % MILLS_IN_MINUTE);
    }

    public Bitmap getMusicImg(){
        if (getUrl()==null||Build.VERSION.SDK_INT<Build.VERSION_CODES.Q) {
            return null;
        }
        try (MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever()) {
            mediaMetadataRetriever.setDataSource(getUrl());
            byte[] picture = mediaMetadataRetriever.getEmbeddedPicture();
            if (picture==null) throw new NullPointerException("专辑图片为空");
            return BitmapFactory.decodeByteArray(picture, 0, picture.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
