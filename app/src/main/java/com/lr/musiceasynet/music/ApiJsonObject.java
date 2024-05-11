package com.lr.musiceasynet.music;

import java.util.List;

public class ApiJsonObject {
    public List<MusicTrack> songs;
    public List<PlayList> playlists;
    public PlayList playlist;
    public List<MusicTrackData> data;
    public class MusicTrackData{
        String url;
        long time;
        long id;
    }
}
