package com.lr.musiceasynet;

import android.support.v4.media.session.PlaybackStateCompat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class MusicPlayerBarViewModel extends ViewModel{
    
    public MutableLiveData<MusicInfo> musicInfoLiveData
            = new MutableLiveData<>(new MusicInfo(null,0,MyApplication.getContext().getString(R.string.no_music_is_playing),MyApplication.getContext().getString(R.string.artist_name),0,0));
    public MutableLiveData<MusicInfo> getMusicInfoLiveData(){
        return musicInfoLiveData;
    }
    public MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isMusicUriNull = new MutableLiveData<>(true);
    public void setMusicInfoLiveData(MusicInfo musicInfo){
        musicInfoLiveData.setValue(musicInfo);
    }

    public MutableLiveData<Boolean> getIsPlaying(){
        return isPlaying;
    }

    public void setIsPlaying(Boolean _isPlaying){
        isPlaying.setValue(_isPlaying);
    }
    public MutableLiveData<Boolean> getIsMusicUriNull(){
        return isMusicUriNull;
    }
    public void setIsMusicUriNull(Boolean isNull){
        isMusicUriNull.setValue(isNull);
    }


    public void playMusicInfos(List<MusicInfo> musicInfos,Integer position,MusicPlayerService musicPlayerService){
        musicPlayerService.playByMusicInfos(musicInfos,position);
    }

    public void playNextMusic(MusicPlayerService musicPlayerService){
        if (Boolean.FALSE.equals(isMusicUriNull.getValue())) {
            musicPlayerService.playNextMusic();
            isPlaying.setValue(musicPlayerService.mediaPlayer.isPlaying());
        }
    }

    public void playPreviousMusic(MusicPlayerService musicPlayerService){
        if (Boolean.FALSE.equals(isMusicUriNull.getValue())) {
            musicPlayerService.playPreviousMusic();
            isPlaying.setValue(musicPlayerService.mediaPlayer.isPlaying());
        }
    }

    public void onBottomMusicBarControllerClick(MusicPlayerService musicPlayerService){
        if (Boolean.FALSE.equals(isMusicUriNull.getValue())) {
            if (!musicPlayerService.mediaPlayer.isPlaying()) {
                musicPlayerService.resumeMusic();
                setIsPlaying(true);
                return;
            }
            musicPlayerService.pauseMusic();
            setIsPlaying(false);
        }
    }

    public void progressChanged(int progress,boolean fromUser,MusicPlayerService musicPlayerService){
        if (fromUser){
            musicPlayerService.mediaPlayer.seekTo(progress);
            musicPlayerService.playbackStateCompat
                    .setState(PlaybackStateCompat.STATE_PLAYING, progress, 1);
            musicPlayerService.mediaSessionCompat
                    .setPlaybackState(musicPlayerService.playbackStateCompat.build());
            //若暂停不先设置播放状态再设置暂停则进度条不会更新
            if (!musicPlayerService.mediaPlayer.isPlaying()) {
                musicPlayerService.playbackStateCompat.setState(PlaybackStateCompat.STATE_PAUSED,
                        musicPlayerService.mediaPlayer.getCurrentPosition(),1);
                musicPlayerService.mediaSessionCompat.setPlaybackState(musicPlayerService.playbackStateCompat.build());
            }
        }
    }

}
