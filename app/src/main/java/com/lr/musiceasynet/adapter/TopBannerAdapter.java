package com.lr.musiceasynet.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.lr.musiceasynet.R;
import com.lr.musiceasynet.music.PlayList;

import java.util.List;

public class TopBannerAdapter extends RecyclerView.Adapter<TopBannerAdapter.BannerViewHolder> {

    public TopBannerAdapter(Activity activity, List<PlayList> list) {
        this.activity = activity;
        this.list = list;
    }

    Activity activity;
    //Context context = MyApplication.getContext();
    List<PlayList> list;

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View bannerview = LayoutInflater.from(activity).inflate(R.layout.top_banner_card, parent, false);
        return new BannerViewHolder(bannerview);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        //holder.bannerImg.setImageBitmap(list.get(position).getCoverImg());
        Glide.with(activity).load(list.get(position).getCoverImgUrl()).into(holder.bannerImg);
        holder.bannerText.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BannerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MaterialCardView materialCardView;
        AppCompatImageView bannerImg;
        TextView bannerText;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            materialCardView = itemView.findViewById(R.id.top_banner_card);
            bannerImg = itemView.findViewById(R.id.banner_img);
            bannerText = itemView.findViewById(R.id.banner_text);
            materialCardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onBannerItemClickListener.onclick(getAdapterPosition());
        }
    }

    OnBannerItemClickListener onBannerItemClickListener;
    public void setOnBannerItemClickListener(OnBannerItemClickListener onBannerItemClickListener){
        this.onBannerItemClickListener = onBannerItemClickListener;
    }
    public interface OnBannerItemClickListener{
        void onclick(int position);
    }
}
