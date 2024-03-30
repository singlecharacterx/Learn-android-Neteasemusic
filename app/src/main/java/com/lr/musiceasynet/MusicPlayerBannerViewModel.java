package com.lr.musiceasynet;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MusicPlayerBannerViewModel extends ViewModel{
    public MutableLiveData<MusicInfo> musicinfolivedata;
    public MutableLiveData<Boolean> isPlaying;
    public MutableLiveData<MusicInfo> getMusicInfoLiveData(){
        if (musicinfolivedata==null){
            musicinfolivedata = new MutableLiveData<>();
            musicinfolivedata.setValue(new MusicInfo(null,0,MyApplication.getContext().getString(R.string.no_music_is_playing),MyApplication.getContext().getString(R.string.artist_name),0,0));
        }
        return musicinfolivedata;
    }

    public void setMusicInfoLiveData(MusicInfo musicInfo){
        musicinfolivedata.setValue(musicInfo);
    }

    public MutableLiveData<Boolean> getIsPlaying(){
        if (isPlaying==null){
            isPlaying = new MutableLiveData<>();
            isPlaying.setValue(false);
        }
        return isPlaying;
    }

    public void setIsPlaying(Boolean _isPlaying){
        isPlaying.setValue(_isPlaying);
    }

}
