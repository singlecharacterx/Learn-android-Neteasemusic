package com.lr.musiceasynet;

import androidx.annotation.Nullable;

public class MusicInfo {
    public MusicInfo(){

    }
    public MusicInfo(String url, long id, String title, String artisst, long duration,long album) {
        this.url = url;
        this.id = id;
        this.title = title;
        this.artisst = artisst;
        this.duration = duration;
        this.album = album;
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
}
