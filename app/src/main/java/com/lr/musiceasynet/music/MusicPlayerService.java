package com.lr.musiceasynet.music;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
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
    private MusicInfo musicInfo;
    private Thread musicThread;

    public List<MusicInfo> getMusicInfos() {
        return musicInfos;
    }

    private List<MusicInfo> musicInfos = new ArrayList<>();
    int musicPosition;

    MediaSessionCompat mediaSessionCompat;

    MediaMetadataCompat.Builder mediadatacompat = new MediaMetadataCompat.Builder();
    public PlaybackStateCompat.Builder playbackStateCompat = new PlaybackStateCompat.Builder();
    MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback() {

        @Override
        public void onPrepare() {
            mediaPlayer.start();
            playbackStateCompat.setState(PlaybackStateCompat.STATE_PLAYING,
                    mediaPlayer.getCurrentPosition(), playbackSpeed);
            setPlaybackState();
            super.onPrepare();
        }

        @Override
        public void onPlay() {
            mediaPlayer.start();
            isPlaying.setValue(true);
            playbackStateCompat.setState(PlaybackStateCompat.STATE_PLAYING,
                    mediaPlayer.getCurrentPosition(), playbackSpeed);
            setPlaybackState();
            super.onPlay();
            Log.d("play",String.valueOf(playbackStateCompat.build().getState()));
        }

        @Override
        public void onPause() {
            mediaPlayer.pause();
            isPlaying.setValue(false);
            playbackStateCompat.setState(PlaybackStateCompat.STATE_PAUSED,
                    mediaPlayer.getCurrentPosition(), playbackSpeed);
            setPlaybackState();
            super.onPause();
            Log.d("pause",String.valueOf(playbackStateCompat.build().getState()));
        }

        @Override
        public void onSkipToNext() {
            playNextMusic();
            Log.d("next",String.valueOf(playbackStateCompat.build().getState()));
            super.onSkipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            playPreviousMusic();
            Log.d("previous",String.valueOf(playbackStateCompat.build().getState()));
            super.onSkipToPrevious();
        }

        @Override
        public void onSeekTo(long pos) {
            mediaPlayer.seekTo((int)pos);
            if (Boolean.TRUE.equals(isPlaying.getValue())){
                playbackStateCompat.setState(PlaybackStateCompat.STATE_PLAYING,
                        mediaPlayer.getCurrentPosition(), playbackSpeed);
                setPlaybackState();
                return;
            }
            playbackStateCompat.setState(PlaybackStateCompat.STATE_PAUSED,
                    mediaPlayer.getCurrentPosition(), playbackSpeed);
            setPlaybackState();
        }

    };

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
        //if (mediaPlayer==null) mediaPlayer = new MediaPlayer();
        mediaSessionCompat = new MediaSessionCompat(MyApplication.getContext(),getString(R.string.app_name));
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        //Log.d("service","Imstart");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaSessionCompat.release();
        mediaPlayer.release();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //循环播放
        playNextMusic();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    //单曲播放
    public void playByMusicInfo(MusicInfo musicInfo){
        this.musicInfo = musicInfo;
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicInfo.getUrl());
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying.postValue(true);//如果活动与服务连接则提醒controller更新ui
            deliverMusicInfo.postValue(musicInfos.get(musicPosition));
            playbackStateCompat.setState(PlaybackStateCompat.STATE_PLAYING,
                    mediaPlayer.getCurrentPosition(), playbackSpeed);
            setPlaybackState();
        } catch (IOException e) {
            Log.e("playByMusicInfoError", "发生IO异常");
            //throw new RuntimeException(e);
        }
    }

    //音乐集合播放
    public void playByMusicInfos(List<MusicInfo> musicInfos, int position){
        //this.musicInfo = musicInfo;
        this.musicInfos = musicInfos;
        musicPosition = position;
        //addToPlayerNotification(musicInfos.get(position));
        playByMusicInfo(musicInfos.get(position));
    }

    public void playByOnlineMusicInfo(MusicInfo musicInfo){
        this.musicInfo = musicInfo;
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicInfo.getUrl());
            Log.d("MP3URL", musicInfo.getUrl());
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying.postValue(true);//如果活动与服务连接则提醒controller更新ui
            deliverMusicInfo.postValue(musicInfo);
            playbackStateCompat.setState(PlaybackStateCompat.STATE_PLAYING,
                    mediaPlayer.getCurrentPosition(), playbackSpeed);
            setPlaybackState();
        } catch (IOException e) {
            Log.e("playByMusicInfoError", "发生IO异常");
            //throw new RuntimeException(e);
        }
    }
    public void playByOnlineMusicInfos(List<MusicInfo> musicInfos, int position){
        //this.musicInfo = musicInfo;
        this.musicInfos = musicInfos;
        musicPosition = position;
        //addToPlayerNotification(musicInfos.get(position));
        playByOnlineMusicInfo(musicInfos.get(position));
    }

    public void pauseMusic(){
        mediaPlayer.pause();
        playbackStateCompat.setState(PlaybackStateCompat.STATE_PAUSED,
                mediaPlayer.getCurrentPosition(), playbackSpeed);
        setPlaybackState();
    }

    public void resumeMusic(){
        if (mediaPlayer.isPlaying()) {
            return;
        }
        mediaPlayer.start();
        playbackStateCompat.setState(PlaybackStateCompat.STATE_PLAYING,
                mediaPlayer.getCurrentPosition(), playbackSpeed);
        setPlaybackState();
    }

    public void stopMusic(){
        mediaPlayer.stop();
    }

    public void playNextMusic(){
        if (musicPosition>=musicInfos.size()-1) {
            musicPosition=0;
        }else musicPosition++;

        if (!musicInfos.isEmpty() &&musicInfos.get(musicPosition).getUrl()!=null){
            //addToPlayerNotification(musicInfos.get(musicPosition));
            playByMusicInfo(musicInfos.get(musicPosition));
        }
    }

    public void playPreviousMusic(){
        if (musicPosition-1>=0) {
            musicPosition--;
        }else musicPosition=musicInfos.size()-1;
        if (!musicInfos.isEmpty() &&musicInfos.get(musicPosition).getUrl()!=null){
            //addToPlayerNotification(musicInfos.get(musicPosition));
            playByMusicInfo(musicInfos.get(musicPosition));
        }
    }

    public void setIsPlayingValue(){
        isPlaying.setValue(mediaPlayer.isPlaying());
    }
    public void setPlaybackState(){
        mediaSessionCompat.setPlaybackState(playbackStateCompat.build());
    }

    private void addToPlayerNotification(MusicInfo musicInfo){
        initMediaSessionCompat(musicInfo);
        Notification notification = null;
        NotificationManager notificationManager =
                (NotificationManager) MyApplication.getContext().getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_STRING_ID)
                    .setSmallIcon(R.drawable.tune_dark)
                    .setLargeIcon(musicInfo.getMusicImg())
                    .setContentTitle(musicInfo.getTitle())
                    .setContentText(musicInfo.getArtisst())
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSessionCompat.
                                    getSessionToken()))
                    .build();
            notificationManager.createNotificationChannel(
                    new NotificationChannel(NOTIFICATION_CHANNEL_STRING_ID
                            ,getString(R.string.app_name),NotificationManager.IMPORTANCE_LOW));
        }//版本适配待做
        startForeground(NOTIFICATION_CHANNEL_ID,notification);
        //notificationManager.notify(NOTIFICATION_CHANNEL_ID,notification);
    }

    private void initMediaSessionCompat(MusicInfo musicInfo){
        mediadatacompat.putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                musicInfo.getDuration());
        mediadatacompat.putText(MediaMetadataCompat.METADATA_KEY_TITLE,
                musicInfo.getTitle());
        mediadatacompat.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                musicInfo.getMusicImg());
        mediaSessionCompat.setMetadata(mediadatacompat.build());
        playbackStateCompat.setState(PlaybackStateCompat.STATE_PLAYING,
                mediaPlayer.getCurrentPosition(), playbackSpeed);
        setPlaybackState();
        //分开设置play和pause可贴合耳机操作 ACTION_PLAY_PAUSE则无法判断
        playbackStateCompat.setActions(PlaybackStateCompat.ACTION_PLAY
                |PlaybackStateCompat.ACTION_PAUSE
                |PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                |PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                |PlaybackStateCompat.ACTION_SEEK_TO);
        mediaSessionCompat.setCallback(callback);
        setPlaybackState();
    }

}
