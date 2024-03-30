package com.lr.musiceasynet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class LocalAlbumFragment extends Fragment {

    MusicPlayerBannerViewModel musicPlayerBannerViewModel;
    RecyclerView localmusicrv;
    List<MusicInfo> musicInfos = new ArrayList<>();
    MusicListRVAdapter musicListRVAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_local_album, container, false);

        localmusicrv = root.findViewById(R.id.local_album_list_RV);
        musicPlayerBannerViewModel = new ViewModelProvider(getActivity()).get(MusicPlayerBannerViewModel.class);

        //fortest
        //musicInfos.add(new MusicInfo("",0,"TestTitle","testartist",0,0));
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_MEDIA_AUDIO},0);
            if (ContextCompat.checkSelfPermission(MyApplication.getContext(),Manifest.permission.READ_MEDIA_AUDIO)== PackageManager.PERMISSION_GRANTED) {
                musicInfos = MusicUtil.getMusicInfos();
            }
        }else if (ContextCompat.checkSelfPermission(MyApplication.getContext(),Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            musicInfos = MusicUtil.getMusicInfos();
        }

        if (musicInfos.isEmpty()){
            Toast.makeText(MyApplication.getContext(),"didntfindlocalmusic",Toast.LENGTH_SHORT).show();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        musicListRVAdapter = new MusicListRVAdapter(getActivity(),musicInfos);
        localmusicrv.setLayoutManager(linearLayoutManager);
        localmusicrv.setAdapter(musicListRVAdapter);

        musicListRVAdapter.setOnMusicItemClickListener(position -> {
            musicPlayerBannerViewModel.setMusicInfoLiveData(musicInfos.get(position));
            musicPlayerBannerViewModel.isPlaying.setValue(true);
            MainActivity.musicPlayerService.playByMusicInfo(musicInfos.get(position));
            //Toast.makeText(MyApplication.getContext(),musicInfos.get(position).getTitle(),Toast.LENGTH_SHORT).show();
        });

        return root;
    }
}