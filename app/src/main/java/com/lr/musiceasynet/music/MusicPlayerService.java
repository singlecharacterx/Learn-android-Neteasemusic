package com.lr.musiceasynet.music;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.lr.musiceasynet.MyApplication;
import com.lr.musiceasynet.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerService extends Service
        implements MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener{
    public static int playbackSpeed = 1;
    public final static int NOTIFICATION_CHANNEL_ID = 1;
    public final static String NOTIFICATION_CHANNEL_STRING_ID = "MusicEasyNetId";
    public MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    public MutableLiveData<MusicInfo> deliverMusicInfo =
            new MutableLiveData<>(new MusicInfo(null,0, MyApplication.getContext().getString(R.string.no_music_is_playing),MyApplication.getContext().getString(R.string.artist_name),0,0));

    public MediaPlayer mediaPlayer;

    private List<MusicInfo> musicInfos = new ArrayList<>();
    int musicPosition;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicPlayerBinder();
    }
    public class MusicPlayerBinder extends Binder{
        public MusicPlayerService getService(){
            return MusicPlayerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //循环播放
        if (!musicInfos.isEmpty()){playNextMusic();}
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    //单曲播放
    public void playByMusicInfo(MusicInfo musicInfo){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicInfo.getUrl());
            mediaPlayer.prepare();
            mediaPlayer.start();
            //如果活动与服务连接则提醒controller更新ui
            isPlaying.postValue(true);
            deliverMusicInfo.postValue(musicInfos.get(musicPosition));
        } catch (IOException e) {
            Log.e("playByMusicInfoError", "发生IO异常");
        }
    }


    public void playByMusicInfos(List<MusicInfo> musicInfos, int position){
        this.musicInfos = musicInfos;
        musicPosition = position;
        playByMusicInfo(musicInfos.get(position));
    }


    public void pauseMusic(){
        mediaPlayer.pause();
    }

    public void resumeMusic(){
        if (mediaPlayer.isPlaying()) {
            return;
        }
        mediaPlayer.start();
    }

    public void playNextMusic(){
        if (musicPosition>=musicInfos.size()-1) {
            musicPosition=0;
        }else {
            musicPosition++;
        }
        playByMusicInfo(musicInfos.get(musicPosition));

    }

    public void playPreviousMusic(){
        if (musicPosition-1>=0) {
            musicPosition--;
        }
        else {
            musicPosition = musicInfos.size() - 1;
        }
        playByMusicInfo(musicInfos.get(musicPosition));

    }

    public void setIsPlayingValue(){
        isPlaying.setValue(mediaPlayer.isPlaying());
    }


}
