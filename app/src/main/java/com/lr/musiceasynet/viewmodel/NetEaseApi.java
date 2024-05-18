package com.lr.musiceasynet.viewmodel;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lr.musiceasynet.music.ApiJsonObject;
import com.lr.musiceasynet.music.MusicTrack;
import com.lr.musiceasynet.music.PlayList;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetEaseApi {
    public static final String API_URL = "http://47.115.207.64:3000/";
    public static final String MP3_URL = "https://music.163.com/song/media/outer/url?id=";
    public static final String GET_CURATED_PLAYLIST = "related/playlist?id=";
    public static final String GET_PLAYLIST_DETAIL = "playlist/detail?id=";
    public static final String GET_TRACKS_DETAILED = "playlist/track/all?limit=1000&id=";
    public static final String GET_TRACKS_URL = "song/url?id=";
    public static final String GET_HIGH_QUALITY_PLAYLIST = "top/playlist/highquality";
    public static final String NO_CONTENT="内容为空";
    public static Thread musicThread;

    public static <T>String getJson(String keyWords,T id){
        String content = NO_CONTENT;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_URL+keyWords+id)
                .get()
                .build();
        try(Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() == 200&&response.body()!=null) {
                content = response.body().string();
                return content;
            }
            Log.d("TestGetJson",content);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TestGetJson",NO_CONTENT);
        }
        return content;
    }

    /*public static String getJsonByRegex(String jsonContent,String regex){
        String content=NO_CONTENT;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jsonContent);
        if(matcher.find()) content = matcher.group();
        return content;
    }*/

    public static List<PlayList> getSongListInfos(String jsonData){
        Gson gson = new Gson();
        return gson.fromJson(jsonData,new TypeToken<List<PlayList>>(){}.getType());
    }
    public static ApiJsonObject getApiJsonObeject(String jsonData){
        Gson gson = new Gson();
        return gson.fromJson(jsonData, ApiJsonObject.class);
    }

    public static List<MusicTrack> getMusicTracksInfos(String jsonData){
        Gson gson = new Gson();
        return gson.fromJson(jsonData,new TypeToken<List<MusicTrack>>(){}.getType());
    }

}
