package com.lr.musiceasynet.viewmodel;

import android.support.v4.media.session.PlaybackStateCompat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lr.musiceasynet.MyApplication;
import com.lr.musiceasynet.R;
import com.lr.musiceasynet.music.MusicInfo;
import com.lr.musiceasynet.music.MusicPlayerService;

import java.util.List;
import java.util.Objects;

public class MusicPlayerBarViewModel extends ViewModel{
    
    public MutableLiveData<MusicInfo> musicInfoLiveData
            = new MutableLiveData<>(new MusicInfo(null,0, MyApplication.getContext().getString(R.string.no_music_is_playing),MyApplication.getContext().getString(R.string.artist_name),0,0));

    public MutableLiveData<MusicInfo> getMusicInfoLiveData(){
        return musicInfoLiveData;
    }
    public MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    public void setMusicInfoLiveData(MusicInfo musicInfo){
        musicInfoLiveData.setValue(musicInfo);
    }

    public void postMusicInfoLiveData(MusicInfo musicInfo){
        musicInfoLiveData.postValue(musicInfo);
    }

    public MutableLiveData<Boolean> getIsPlaying(){
        return isPlaying;
    }

    public void setIsPlaying(Boolean _isPlaying){
        isPlaying.setValue(_isPlaying);
    }


    public void playMusicInfos(List<MusicInfo> musicInfos, Integer position, MusicPlayerService musicPlayerService){
        musicPlayerService.playByMusicInfos(musicInfos,position);
    }


    public void playNextMusic(MusicPlayerService musicPlayerService){
        if (Objects.requireNonNull(musicInfoLiveData.getValue()).getUrl()==null) {
            return;
        }
        musicPlayerService.playNextMusic();
        musicPlayerService.setIsPlayingValue();
    }

    public void playPreviousMusic(MusicPlayerService musicPlayerService){
        if (Objects.requireNonNull(musicInfoLiveData.getValue()).getUrl()==null) {
            return;
        }
        musicPlayerService.playPreviousMusic();
        musicPlayerService.setIsPlayingValue();
    }

    public void onBottomMusicBarControllerClick(MusicPlayerService musicPlayerService){
        if (Objects.requireNonNull(musicInfoLiveData.getValue()).getUrl()==null) {
            return;
        }
        if (!musicPlayerService.mediaPlayer.isPlaying()) {
            musicPlayerService.resumeMusic();
            setIsPlaying(true);
        }else {
            musicPlayerService.pauseMusic();
            setIsPlaying(false);
        }
    }

    public void progressChanged(int progress,boolean fromUser,MusicPlayerService musicPlayerService){
        if (!fromUser){
            return;
        }
        musicPlayerService.mediaPlayer.seekTo(progress);
        musicPlayerService.playbackStateCompat
                .setState(PlaybackStateCompat.STATE_PLAYING, progress, MusicPlayerService.playbackSpeed);
        musicPlayerService.setPlaybackState();
        //若暂停不先设置播放状态再设置暂停则进度条不会更新
        if (!musicPlayerService.mediaPlayer.isPlaying()) {
            musicPlayerService.playbackStateCompat.setState(PlaybackStateCompat.STATE_PAUSED,
                    musicPlayerService.mediaPlayer.getCurrentPosition(),MusicPlayerService.playbackSpeed);
            musicPlayerService.setPlaybackState();
        }
    }

}
