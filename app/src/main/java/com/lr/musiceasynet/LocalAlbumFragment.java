package com.lr.musiceasynet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lr.musiceasynet.Util.MusicUtil;

import java.util.ArrayList;
import java.util.List;


public class LocalAlbumFragment extends Fragment {

    MusicPlayerBannerViewModel musicPlayerBannerViewModel;
    RecyclerView localmusicrv;
    List<MusicInfo> musicInfos = new ArrayList<>();
    MusicListRVAdapter musicListRVAdapter;
    private View root;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_local_album, container, false);

        localmusicrv = root.findViewById(R.id.local_album_list_RV);
        musicPlayerBannerViewModel = new ViewModelProvider(getActivity()).get(MusicPlayerBannerViewModel.class);

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS},0);

        checkPermissionApi33();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        musicListRVAdapter = new MusicListRVAdapter(getActivity(),musicInfos);
        localmusicrv.setLayoutManager(linearLayoutManager);
        localmusicrv.setAdapter(musicListRVAdapter);

        musicListRVAdapter.setOnMusicItemClickListener(position -> {
            musicPlayerBannerViewModel.setMusicInfoLiveData(musicInfos.get(position));
            MainActivity.musicPlayerService.playByMusicInfos(musicInfos,position,musicPlayerBannerViewModel);
        });

        return root;
    }

    private void checkPermissionApi33() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(MyApplication.getContext(),Manifest.permission.READ_MEDIA_AUDIO)== PackageManager.PERMISSION_GRANTED) {
                musicInfos = MusicUtil.getMusicInfos();
                checkMusicInfoIsEmptyThenAlert();
            }else if (ContextCompat.checkSelfPermission(MyApplication.getContext(),Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                musicInfos = MusicUtil.getMusicInfos();
                checkMusicInfoIsEmptyThenAlert();
            }else {
                Toast.makeText(MyApplication.getContext(), getString(R.string.please_give_music_permisson),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkMusicInfoIsEmptyThenAlert(){
        if (musicInfos.isEmpty()){
            Toast.makeText(MyApplication.getContext(), getString(R.string.didnt_find_music),Toast.LENGTH_SHORT).show();
        }
    }

}