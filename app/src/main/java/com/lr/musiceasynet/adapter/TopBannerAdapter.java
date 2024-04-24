package com.lr.musiceasynet.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.lr.musiceasynet.R;

import java.util.List;

public class TopBannerAdapter extends RecyclerView.Adapter<TopBannerAdapter.BannerViewHolder> {

    TopBannerAdapter(){

    }

    public TopBannerAdapter(Activity activity, List<Integer> list){
        this.activity = activity;
        this.list = list;
    }
    Activity activity;
    //Context context = MyApplication.getContext();
    List<Integer> list;

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View bannerview = LayoutInflater.from(activity).inflate(R.layout.top_banner_card,parent,false);
        return new BannerViewHolder(bannerview);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.bannerimg.setImageResource(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BannerViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView materialCardView;
        AppCompatImageView bannerimg;
        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            materialCardView = itemView.findViewById(R.id.top_banner_card);
            bannerimg = itemView.findViewById(R.id.banner_img);
        }
    }
}
