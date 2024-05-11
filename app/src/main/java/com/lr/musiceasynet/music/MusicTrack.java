package com.lr.musiceasynet.music;

import android.util.Log;

import androidx.annotation.NonNull;

import com.lr.musiceasynet.viewmodel.NetEaseApi;

import java.util.ArrayList;
import java.util.List;

public class MusicTrack {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Artist> getAr() {
        return ar;
    }

    public String getAllArtist(){
        StringBuilder artists= new StringBuilder();
        for (Artist artist:ar){
            if (artists.length() == 0) artists = new StringBuilder(artist.name);
            else artists.insert(0, artist.name + "/");
        }
        return artists.toString();
    }

    public void setAr(List<Artist> ar) {
        this.ar = ar;
    }

    public Album getAl() {
        return al;
    }

    public void setAl(Album al) {
        this.al = al;
    }

    String name;
    long id;
    List<Artist> ar;
    Album al;
    boolean isSelected =false;



    public MusicTrack(String name, long id, List<Artist> ar, Album al) {
        this.name = name;
        this.id = id;
        this.ar = ar;
        this.al = al;
    }

    public class Artist {
        String name;
        long id;

        public Artist(String name, long id) {
            this.name = name;
            this.id = id;
        }

    }

    public class Album {
        String name;
        long id;
        String picUrl;

        public Album(String name, long id, String picUrl) {
            this.name = name;
            this.id = id;
            this.picUrl = picUrl;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return this.name + this.id + "\n" + ar.get(0).name + ar.get(0).id + "\n" + al.name + al.picUrl + al.id;
    }

    public static List<MusicInfo> alterTracksToInfos(List<MusicTrack> musicTracks){
        List<MusicInfo> musicInfos = new ArrayList<>();
        StringBuilder idString = new StringBuilder(String.valueOf(musicTracks.get(0).getId()));
        for (int k=1;k<musicTracks.size();k++){
            idString.append("&id=").append(musicTracks.get(k).getId());
        }
        Log.d("idString", idString.toString());
        String json = NetEaseApi.getJson(NetEaseApi.GET_TRACKS_URL, idString.toString());
        ApiJsonObject apiJsonObject = NetEaseApi.getApiJsonObeject(json);
        for (int i=0;i< musicTracks.size();i++){
            long id = musicTracks.get(i).getId();
            String title = musicTracks.get(i).getName();
            String artists = musicTracks.get(i).getAllArtist();
            int i2 = 0;
            for (int j = 0; j< apiJsonObject.data.size(); j++){
                if (id== apiJsonObject.data.get(j).id) i2=j;
            }//进一步获取歌曲持续时间和url时，id乱序，通过判断
            String mp3Url = apiJsonObject.data.get(i2).url;
            long duration = apiJsonObject.data.get(i2).time;
            musicInfos.add(new MusicInfo(mp3Url,i,title,artists,duration,1));
            Log.d("apiJsonObjectID", String.valueOf(apiJsonObject.data.get(i).id));
        }
        return musicInfos;
    }

    public static MusicInfo alterTracksToInfo(MusicTrack musicTrack){
        long id = musicTrack.getId();
        String json = NetEaseApi.getJson(NetEaseApi.GET_TRACKS_URL,id);
        ApiJsonObject apiJsonObject = NetEaseApi.getApiJsonObeject(json);
        String mp3Url = apiJsonObject.data.get(0).url;
        String title = musicTrack.getName();
        String artists = musicTrack.getAllArtist();
        long duration = apiJsonObject.data.get(0).time;

        return new MusicInfo(mp3Url,id,title,artists,duration,1);
    }
}
