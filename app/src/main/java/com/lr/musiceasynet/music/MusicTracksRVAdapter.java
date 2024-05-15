package com.lr.musiceasynet.music;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.lr.musiceasynet.R;

import java.util.List;

public class MusicTracksRVAdapter extends RecyclerView.Adapter<MusicTracksRVAdapter.MusicTracksViewHolder> {
    Activity activity;
    List<MusicTrack> musicTracks;
    int isSelectedPosition = -1;

    public MusicTracksRVAdapter(Activity activity, List<MusicTrack> musicTracks) {
        this.activity = activity;
        this.musicTracks = musicTracks;
    }

    @NonNull
    @Override
    public MusicTracksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.music_list_item, parent, false);
        return new MusicTracksRVAdapter.MusicTracksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicTracksViewHolder holder, int position) {
        holder.idtext.setText(String.valueOf(position+1));
        holder.titletext.setText(musicTracks.get(position).getName());
        holder.artisttext.setText(musicTracks.get(position).getAllArtist());
        holder.durationtext.setText(musicTracks.get(position).getAl().name);
        /*if (position == isSelectedPosition){
            holder.materialCardView.setCardBackgroundColor(activity.getResources().getColor(R.color.grey_transparent));
        }else {
            holder.materialCardView.setCardBackgroundColor(activity.getResources().getColor(R.color.transparent));
        }*/
    }

    @Override
    public int getItemCount() {
        return musicTracks.size();
    }


    public class MusicTracksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MaterialCardView materialCardView;
        TextView idtext,titletext,artisttext,durationtext;
        public MusicTracksViewHolder(@NonNull View itemView) {
            super(itemView);
            materialCardView = itemView.findViewById(R.id.music_list_item_card);
            idtext = itemView.findViewById(R.id.musicitemid);
            titletext = itemView.findViewById(R.id.musicitemtitle);
            artisttext = itemView.findViewById(R.id.musicitemartisst);
            durationtext = itemView.findViewById(R.id.musicitemduration);
            idtext.setVisibility(View.VISIBLE);

            materialCardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onClick(getAdapterPosition());
            isSelectedPosition = getAdapterPosition();
            //notifyDataSetChanged();

        }
    }

    OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener{
        void onClick(int position);
    }
}
