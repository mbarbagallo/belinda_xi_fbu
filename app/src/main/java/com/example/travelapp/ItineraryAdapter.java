package com.example.travelapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import java.util.List;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ViewHolder> {
    private static final String TAG = "ItineraryAdapter";
    private Context context;
    private List<Itinerary> itineraries;

    public ItineraryAdapter(Context context, List<Itinerary> itineraries) {
        this.context = context;
        this.itineraries = itineraries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_itinerary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItineraryAdapter.ViewHolder holder, int position) {
        Itinerary itinerary = itineraries.get(position);
        holder.bind(itinerary);
    }

    @Override
    public int getItemCount() {
        return itineraries.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle;
        private TextView tvLocations;
        private TextView tvDistance;
        private ImageView ivPhoto;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLocations = itemView.findViewById(R.id.tvLocations);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }

        public void bind(Itinerary itinerary) {
            tvTitle.setText(itinerary.getTitle());
            tvLocations.setText(itinerary.getLocations());
            // TODO - temporary distance - change once done w algo
            tvDistance.setText("500 mi");
            ParseFile image = itinerary.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivPhoto);
            }
        }
    }
}
