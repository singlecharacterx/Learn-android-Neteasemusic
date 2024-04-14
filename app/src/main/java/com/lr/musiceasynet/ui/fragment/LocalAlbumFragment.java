package com.lr.musiceasynet.ui.fragment;

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

import com.lr.musiceasynet.MainActivity;
import com.lr.musiceasynet.MusicInfo;
import com.lr.musiceasynet.MusicListRVAdapter;
import com.lr.musiceasynet.MusicPlayerBarViewModel;
import com.lr.musiceasynet.MyApplication;
import com.lr.musiceasynet.R;
import com.lr.musiceasynet.music.DealMusicInfo;

import java.util.ArrayList;
import java.util.List;


public class LocalAlbumFragment extends Fragment {

    MusicPlayerBarViewModel musicPlayerBarViewModel;
    RecyclerView localmusicrv;
    List<MusicInfo> musicInfos = new ArrayList<>();
    MusicListRVAdapter musicListRVAdapter;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_local_album, container, false);
        localmusicrv = root.findViewById(R.id.local_album_list_RV);
        musicPlayerBarViewModel = new ViewModelProvider(requireActivity()).get(MusicPlayerBarViewModel.class);
        checkPermission();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        musicListRVAdapter = new MusicListRVAdapter(requireActivity(),musicInfos);
        localmusicrv.setLayoutManager(linearLayoutManager);
        localmusicrv.setAdapter(musicListRVAdapter);
        musicListRVAdapter.setOnMusicItemClickListener(position -> {
            musicPlayerBarViewModel.setMusicInfoLiveData(musicInfos.get(position));
            musicPlayerBarViewModel.setIsMusicUriNull(false);
            musicPlayerBarViewModel.playMusicInfos(musicInfos,position,
                    ((MainActivity)requireActivity()).getBindedService());
        });

        return root;
    }

    private void checkPermission(){
        if (Build.VERSION.SDK_INT>=33){
            checkPermissionApi33();
        }else {
            checkPermissionUnderApi33();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void checkPermissionApi33() {
        if (ContextCompat.checkSelfPermission(MyApplication.getContext(),
                Manifest.permission.READ_MEDIA_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MyApplication.getContext(), getString(R.string.please_give_music_permisson),Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS},0);
            return;
        }
        musicInfos = DealMusicInfo.getMusicInfos();
        checkMusicInfoIsEmptyThenAlert();

    }


    private void checkPermissionUnderApi33(){
        if (ContextCompat.checkSelfPermission(MyApplication.getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MyApplication.getContext(), getString(R.string.please_give_music_permisson),Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE},0);
            return;
        }
        musicInfos = DealMusicInfo.getMusicInfos();
        checkMusicInfoIsEmptyThenAlert();
    }

    private void checkMusicInfoIsEmptyThenAlert(){
        if (musicInfos.isEmpty()){
            Toast.makeText(MyApplication.getContext(), getString(R.string.didnt_find_music),Toast.LENGTH_SHORT).show();
        }
    }

}