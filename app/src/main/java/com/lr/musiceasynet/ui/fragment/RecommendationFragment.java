package com.lr.musiceasynet.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lr.musiceasynet.R;
import com.lr.musiceasynet.adapter.TopBannerAdapter;
import com.lr.musiceasynet.music.ApiJsonObject;
import com.lr.musiceasynet.music.PlayList;
import com.lr.musiceasynet.viewmodel.NetEaseApi;
import com.lr.musiceasynet.viewmodel.PlayListViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RecommendationFragment extends Fragment {
    private final int UPDATE_TOP_BANNER = 0;
    public RecyclerView recommendBanner;
    public List<PlayList> bannerlist = new ArrayList<>();
    private View root;
    public TopBannerAdapter topBannerAdapter;
    PlayListViewModel playListViewModel;
    private ApiJsonObject apiJsonObject;
    private String json;
    private Handler handler;
    private Message message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_recommandation, container, false);
        init();
        initRecommendBanner();
        setHandler();
        return root;
    }

    void setHandler(){
        handler = new Handler(Objects.requireNonNull(Looper.myLooper())){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case UPDATE_TOP_BANNER:
                        bannerlist = (List<PlayList>)msg.obj;
                        topBannerAdapter = new TopBannerAdapter(requireActivity(),bannerlist);
                        recommendBanner.setAdapter(topBannerAdapter);
                        topBannerAdapter.setOnBannerItemClickListener(position -> onBannerItemClick(position));
                        break;
                }
            }
        };
    }

    void initRecommendBanner() {
        new Thread(()->{
            json = NetEaseApi.getJson(NetEaseApi.GET_HIGH_QUALITY_PLAYLIST,null);
            Log.d("TEST", json);
            if (!json.equals(NetEaseApi.NO_CONTENT)){
                apiJsonObject = NetEaseApi.getApiJsonObeject(json);
                bannerlist = apiJsonObject.playlists;
                message = new Message();
                message.what = UPDATE_TOP_BANNER;
                message.obj = bannerlist;
                handler.sendMessage(message);
            }
        }).start();
    }

    void init(){
        recommendBanner = root.findViewById(R.id.recommend_banner);
        topBannerAdapter = new TopBannerAdapter(requireActivity(), bannerlist);
        playListViewModel = new ViewModelProvider(this).get(PlayListViewModel.class);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recommendBanner.setAdapter(topBannerAdapter);
        recommendBanner.setLayoutManager(linearLayoutManager);
    }

    public void onBannerItemClick(int position){
        Bundle bundle = new Bundle();
        bundle.putString("picUrl",bannerlist.get(position).getCoverImgUrl());
        bundle.putString("name",bannerlist.get(position).getName());
        bundle.putLong("id", bannerlist.get(position).getId());
        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        navController.setGraph(R.navigation.bottomnavigation);
        navController.navigate(R.id.action_navigationrecommandpage_to_navigationPlaylist,bundle);

    }

}