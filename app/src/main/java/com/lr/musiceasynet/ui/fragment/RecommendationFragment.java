package com.lr.musiceasynet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.lr.musiceasynet.R;
import com.lr.musiceasynet.adapter.TopBannerAdapter;

import java.util.ArrayList;
import java.util.List;


public class RecommendationFragment extends Fragment {

    RecyclerView recommendBanner;
    List<Integer> bannerlist = new ArrayList<>();
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_recommandation, container, false);
        recommendBanner = root.findViewById(R.id.recommend_banner);

        bannerlist.add(R.drawable.ic_launcher_background);
        bannerlist.add(R.drawable.account_dark);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recommendBanner.setAdapter(new TopBannerAdapter(requireActivity(), bannerlist));
        recommendBanner.setLayoutManager(linearLayoutManager);
        new LinearSnapHelper().attachToRecyclerView(recommendBanner);

        return root;
    }
}