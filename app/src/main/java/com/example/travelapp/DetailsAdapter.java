package com.example.travelapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder> {

    private static final String TAG = "DetailsAdapter";
    private Context context;
    private List<String> destinations;
    private List<String> distances;

    public DetailsAdapter(Context context, Details details) {
        this.context = context;
        this.destinations = details.getDestinations();
        this.distances = details.getDistances();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_details, parent, false);
        return new DetailsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsAdapter.ViewHolder holder, int position) {
        String distance = distances.get(position);
        String destination1 = destinations.get(position);
        String destination2 = destinations.get(position + 1);
        holder.bind(distance, destination1, destination2);
    }

    @Override
    public int getItemCount() {
        return distances.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvDestination1;
        private TextView tvDestination2;
        private TextView tvDistance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDistance = itemView.findViewById(R.id.tvLegDistance);
            tvDestination1 = itemView.findViewById(R.id.tvDestination1);
            tvDestination2 = itemView.findViewById(R.id.tvDestination2);
        }

        public void bind(String distance, String destination1, String destination2) {
            tvDistance.setText(distance);
            tvDestination1.setText(destination1);
            tvDestination2.setText(destination2);
        }
    }
}
