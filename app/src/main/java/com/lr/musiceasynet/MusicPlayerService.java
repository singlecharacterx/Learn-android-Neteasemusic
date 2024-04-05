package com.lr.musiceasynet;

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

import com.lr.musiceasynet.Util.MusicUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerService extends Service implements MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener{

    MediaPlayer mediaPlayer;
    private MusicInfo musicInfo;

    public List<MusicInfo> getMusicInfos() {
        return musicInfos;
    }

    private List<MusicInfo> musicInfos = new ArrayList<>();
    int musicPosition;

    MusicPlayerBannerViewModel musicPlayerBannerViewModel;

    MediaSessionCompat mediaSessionCompat;

    MediaMetadataCompat.Builder mediadatacompat = new MediaMetadataCompat.Builder();
    PlaybackStateCompat.Builder playbackStateCompat = new PlaybackStateCompat.Builder();
    MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback() {

        @Override
        public void onPrepare() {
            if (playbackStateCompat.build().getState()!=PlaybackStateCompat.STATE_PLAYING){
                mediaPlayer.start();
                musicPlayerBannerViewModel.isPlaying.setValue(true);
                playbackStateCompat.setState(PlaybackStateCompat.STATE_PLAYING,mediaPlayer.getCurrentPosition(),1);
                mediaSessionCompat.setPlaybackState(playbackStateCompat.build());
            }
            super.onPrepare();
        }

        @Override
        public void onPlay() {
            if (playbackStateCompat.build().getState()!=PlaybackStateCompat.STATE_PLAYING){
                mediaPlayer.start();
                musicPlayerBannerViewModel.isPlaying.setValue(true);
                playbackStateCompat.setState(PlaybackStateCompat.STATE_PLAYING,mediaPlayer.getCurrentPosition(),1);
                mediaSessionCompat.setPlaybackState(playbackStateCompat.build());
            }
            super.onPlay();
            Log.d("play",String.valueOf(playbackStateCompat.build().getState()));
        }

        @Override
        public void onPause() {
            if (playbackStateCompat.build().getState()!=PlaybackStateCompat.STATE_PAUSED){
                mediaPlayer.pause();
                musicPlayerBannerViewModel.isPlaying.setValue(false);
                playbackStateCompat.setState(PlaybackStateCompat.STATE_PAUSED,mediaPlayer.getCurrentPosition(),1);
                mediaSessionCompat.setPlaybackState(playbackStateCompat.build());
            }
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
            playbackStateCompat.setState(PlaybackStateCompat.STATE_PLAYING,mediaPlayer.getCurrentPosition(),1);
            mediaSessionCompat.setPlaybackState(playbackStateCompat.build());
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
            musicPlayerBannerViewModel.isPlaying.setValue(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //音乐集合播放
    public void playByMusicInfos(List<MusicInfo> musicInfos,int position,MusicPlayerBannerViewModel musicPlayerBannerViewModel){
        //this.musicInfo = musicInfo;
        this.musicInfos = musicInfos;
        musicPosition = position;
        this.musicPlayerBannerViewModel = musicPlayerBannerViewModel;
        addToPlayerNotification(musicInfos.get(position));
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicInfos.get(position).getUrl());
            mediaPlayer.prepare();
            mediaPlayer.start();
            musicPlayerBannerViewModel.isPlaying.setValue(true);
            playbackStateCompat.setState(PlaybackStateCompat.STATE_PLAYING,mediaPlayer.getCurrentPosition(),1);
            mediaSessionCompat.setPlaybackState(playbackStateCompat.build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void pauseMusic(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            musicPlayerBannerViewModel.isPlaying.setValue(false);
            playbackStateCompat.setState(PlaybackStateCompat.STATE_PAUSED,mediaPlayer.getCurrentPosition(),1);
            mediaSessionCompat.setPlaybackState(playbackStateCompat.build());
        }
    }

    public void resumeMusic(){
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            musicPlayerBannerViewModel.isPlaying.setValue(false);
            playbackStateCompat.setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), 1);
            mediaSessionCompat.setPlaybackState(playbackStateCompat.build());
        }
    }

    public void stopMusic(){
        mediaPlayer.stop();
    }

    void playNextMusic(){
        if (musicInfos.size()-1>musicPosition) {
            musicPosition++;
        }else {
            musicPosition=0;
        }
        if (!musicInfos.isEmpty() &&musicInfos.get(musicPosition).getUrl()!=null){
            addToPlayerNotification(musicInfos.get(musicPosition));
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(musicInfos.get(musicPosition).getUrl());
                mediaPlayer.prepare();
                mediaPlayer.start();
                musicPlayerBannerViewModel.setMusicInfoLiveData(musicInfos.get(musicPosition));
                playbackStateCompat.setState(PlaybackStateCompat.STATE_PLAYING,mediaPlayer.getCurrentPosition(),1);
                mediaSessionCompat.setPlaybackState(playbackStateCompat.build());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    void playPreviousMusic(){
        if (musicPosition-1>=0) {
            musicPosition--;
        }
        if (!musicInfos.isEmpty() &&musicInfos.get(musicPosition).getUrl()!=null){
            addToPlayerNotification(musicInfos.get(musicPosition));
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(musicInfos.get(musicPosition).getUrl());
                mediaPlayer.prepare();
                mediaPlayer.start();
                musicPlayerBannerViewModel.setMusicInfoLiveData(musicInfos.get(musicPosition));
                playbackStateCompat.setState(PlaybackStateCompat.STATE_PLAYING,mediaPlayer.getCurrentPosition(),1);
                mediaSessionCompat.setPlaybackState(playbackStateCompat.build());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void addToPlayerNotification(MusicInfo musicInfo){
        mediadatacompat.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, musicInfo.getDuration());
        mediadatacompat.putText(MediaMetadataCompat.METADATA_KEY_TITLE,musicInfo.getTitle());
        mediaSessionCompat.setMetadata(mediadatacompat.build());
        playbackStateCompat.setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(),1);
        mediaSessionCompat.setPlaybackState(playbackStateCompat.build());
        playbackStateCompat.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE
                |PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                |PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                |PlaybackStateCompat.ACTION_SEEK_TO);
        mediaSessionCompat.setCallback(callback);
        mediaSessionCompat.setPlaybackState(playbackStateCompat.build());
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            notification = new NotificationCompat.Builder(this,"0")
                    .setSmallIcon(R.drawable.tune_dark)
                    .setLargeIcon(MusicUtil.getMusicImg(musicInfo))
                    .setContentTitle(musicInfo.getTitle())
                    .setContentText(musicInfo.getArtisst())
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSessionCompat.getSessionToken()).setShowActionsInCompactView(0,1,2))
                    .build();
        }
        NotificationManager notificationManager = (NotificationManager) MyApplication.getContext().getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("0",getString(R.string.app_name),NotificationManager.IMPORTANCE_LOW));
        }
        notificationManager.notify(0,notification);

    }

}
