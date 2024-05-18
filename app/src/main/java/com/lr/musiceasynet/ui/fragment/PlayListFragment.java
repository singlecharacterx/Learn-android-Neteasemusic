package com.lr.musiceasynet.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lr.musiceasynet.R;
import com.lr.musiceasynet.music.ApiJsonObject;
import com.lr.musiceasynet.music.MusicInfo;
import com.lr.musiceasynet.music.MusicTrack;
import com.lr.musiceasynet.music.MusicTracksRVAdapter;
import com.lr.musiceasynet.music.PlayList;
import com.lr.musiceasynet.ui.activity.MainActivity;
import com.lr.musiceasynet.viewmodel.MusicPlayerBarViewModel;
import com.lr.musiceasynet.viewmodel.NetEaseApi;
import com.lr.musiceasynet.viewmodel.PlayListViewModel;

import java.util.ArrayList;
import java.util.List;


public class PlayListFragment extends Fragment {
    private View root;
    RecyclerView recyclerView;
    List<MusicTrack> musicTracks = new ArrayList<>();
    PlayListViewModel playListViewModel;
    MusicPlayerBarViewModel musicPlayerBarViewModel;
    MusicTracksRVAdapter musicListRVAdapter;
    long playlistId;
    String playlistName, playlistPicUrl, playlistDescription;
    ImageView playlistCover;
    TextView playlistTitleText, playlistDescriptionText;
    private ApiJsonObject jsonObject;
    private String json;
    private List<MusicInfo> musicInfos;
    boolean isLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_play_list, container, false);
        init();
        initRV();
        initTopContent();

        return root;
    }



    void init() {
        recyclerView = root.findViewById(R.id.musicTracksRV);
        playlistCover = root.findViewById(R.id.playlistImg);
        playlistTitleText = root.findViewById(R.id.playlistTitle);
        playlistDescriptionText = root.findViewById(R.id.playlistDescription);
        playListViewModel = new ViewModelProvider(this).get(PlayListViewModel.class);
        musicListRVAdapter = new MusicTracksRVAdapter(requireActivity(), musicTracks);
        musicPlayerBarViewModel = new ViewModelProvider(requireActivity()).get(MusicPlayerBarViewModel.class);

        playlistId = getArguments().getLong("id");
        playlistName = getArguments().getString("name");
        playlistPicUrl = getArguments().getString("picUrl");
    }

    void initRV(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        new Thread(() -> {
            Log.d("id", String.valueOf(playlistId));
            json = NetEaseApi.getJson(NetEaseApi.GET_TRACKS_DETAILED, playlistId);
            Log.d("json", json);
            if (!json.equals(NetEaseApi.NO_CONTENT)) {
                jsonObject = NetEaseApi.getApiJsonObeject(json);
                musicTracks = jsonObject.songs;
                musicInfos = MusicTrack.alterTracksToInfos(musicTracks);
                requireActivity().runOnUiThread(()->{
                    musicListRVAdapter = new MusicTracksRVAdapter(requireActivity(), musicTracks);
                    recyclerView.setAdapter(musicListRVAdapter);
                    musicListRVAdapter.setOnItemClickListener(position -> {
                        new Thread(()-> {
                            musicPlayerBarViewModel.playMusicInfos(
                                    musicInfos,
                                    position,
                                    ((MainActivity) requireActivity()).getBindedService());
                        }).start();
                    });
                });
            }
        }).start();
    }

    void initTopContent(){
        new Thread(() -> {
            String selectedPlaylistJson =
                    NetEaseApi.getJson(NetEaseApi.GET_PLAYLIST_DETAIL, playlistId);
            ApiJsonObject apiJsonObject = NetEaseApi.getApiJsonObeject(selectedPlaylistJson);
            PlayList playList = apiJsonObject.playlist;
            playlistDescription = playList.getDescription();
            playlistDescription = playlistDescription.replace("\\n", "\n");
            requireActivity().runOnUiThread(()->{
                Glide.with(PlayListFragment.this).load(playlistPicUrl).into(playlistCover);
                playlistDescriptionText.setText(playlistDescription);
                playlistTitleText.setText(playlistName);
            });
        }).start();
    }

    @Override
    public void onDestroyView() {
        if (!isLoading) super.onDestroyView();
    }
}