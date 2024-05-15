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

import com.lr.musiceasynet.ui.activity.MainActivity;
import com.lr.musiceasynet.music.MusicInfo;
import com.lr.musiceasynet.music.MusicListRVAdapter;
import com.lr.musiceasynet.viewmodel.MusicPlayerBarViewModel;
import com.lr.musiceasynet.R;
import com.lr.musiceasynet.music.MusicInfosProvider;

import java.util.ArrayList;
import java.util.List;


public class LocalAlbumFragment extends Fragment {

    MusicPlayerBarViewModel musicPlayerBarViewModel;
    RecyclerView localmusicrv;
    public final static int SDK33 = 33;
    public final static int PERMISSION_REQUEST_CODE=0;
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
            new Thread(()-> {
                musicPlayerBarViewModel.postMusicInfoLiveData(musicInfos.get(position));
                musicPlayerBarViewModel.playMusicInfos(musicInfos, position,
                        ((MainActivity) requireActivity()).getBindedService());
            }).start();
        });

        return root;
    }

    private void checkPermission(){
        if (Build.VERSION.SDK_INT>=SDK33){
            checkPermissionApi33();
        }else {
            checkPermissionUnderApi33();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void checkPermissionApi33() {
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.READ_MEDIA_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireActivity(), getString(R.string.please_give_music_permisson),Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS},PERMISSION_REQUEST_CODE);
            return;
        }
        musicInfos = MusicInfosProvider.getMusicInfos();
        checkMusicInfoIsEmptyThenAlert();

    }


    private void checkPermissionUnderApi33(){
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireActivity(), getString(R.string.please_give_music_permisson),Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
            return;
        }
        musicInfos = MusicInfosProvider.getMusicInfos();
        checkMusicInfoIsEmptyThenAlert();
    }

    private void checkMusicInfoIsEmptyThenAlert(){
        if (musicInfos.isEmpty()){
            Toast.makeText(requireActivity(), getString(R.string.didnt_find_music),Toast.LENGTH_SHORT).show();
        }
    }

}