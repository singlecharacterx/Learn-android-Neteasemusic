package com.lr.musiceasynet;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class MusicListRVAdapter extends RecyclerView.Adapter<MusicListRVAdapter.MusicListViewHoder>{


    Activity activity;
    List<MusicInfo> musicinfos;
    public MusicListRVAdapter(Activity activity,List<MusicInfo> musicinfos) {
        this.activity = activity;
        this.musicinfos = musicinfos;
    }

    @NonNull
    @Override
    public MusicListViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View musiclistview = LayoutInflater.from(activity).inflate(R.layout.music_list_item,parent,false);
        return new MusicListViewHoder(musiclistview);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicListViewHoder holder, int position) {
        holder.idtext.setText(String.valueOf(musicinfos.get(position).getId()));
        holder.titletext.setText(musicinfos.get(position).getTitle());
        holder.artisttext.setText(musicinfos.get(position).getArtisst());
        holder.durationtext.setText(MusicInfo.formatTime(musicinfos.get(position).getDuration()));
    }

    @Override
    public int getItemCount() {
        return musicinfos.size();
    }

    public class MusicListViewHoder extends RecyclerView.ViewHolder implements View.OnClickListener{
        MaterialCardView materialCardView;
        TextView idtext,titletext,artisttext,durationtext;
        public MusicListViewHoder(@NonNull View itemView) {
            super(itemView);
            materialCardView = itemView.findViewById(R.id.music_list_item_card);
            idtext = itemView.findViewById(R.id.musicitemid);
            titletext = itemView.findViewById(R.id.musicitemtitle);
            artisttext = itemView.findViewById(R.id.musicitemartisst);
            durationtext = itemView.findViewById(R.id.musicitemduration);

            materialCardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onMusicItemClickListener.onclick(getAdapterPosition());
        }
    }

    OnMusicItemClickListener onMusicItemClickListener;
    public void setOnMusicItemClickListener(OnMusicItemClickListener onMusicItemClickListener){
        this.onMusicItemClickListener = onMusicItemClickListener;
    }

    public interface OnMusicItemClickListener{
        void onclick(int position);
    }
}
