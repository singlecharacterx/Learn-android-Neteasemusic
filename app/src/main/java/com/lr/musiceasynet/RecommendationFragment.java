package com.lr.musiceasynet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class RecommendationFragment extends Fragment {

    RecyclerView recommandbanner;
    List<Integer> bannerlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recommandation, container, false);

        recommandbanner = root.findViewById(R.id.recommend_banner);
        bannerlist = new ArrayList<>();
        bannerlist.add(R.drawable.ic_launcher_background);
        bannerlist.add(R.drawable.account_dark);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recommandbanner.setAdapter(new TopBannerAdapter(getActivity(), bannerlist));
        recommandbanner.setLayoutManager(linearLayoutManager);
        new LinearSnapHelper().attachToRecyclerView(recommandbanner);

        return root;
    }
}