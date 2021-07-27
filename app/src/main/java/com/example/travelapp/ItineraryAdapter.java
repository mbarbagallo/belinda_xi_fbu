package com.example.travelapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelapp.fragments.DetailsFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

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

    class ViewHolder extends RecyclerView.ViewHolder {
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
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.i(TAG, "long click");
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Itinerary itinerary = itineraries.get(position);
                        queryDetail(itinerary);
                    }
                    return true;
                }
            });
        }

        public void bind(Itinerary itinerary) {
            tvTitle.setText(itinerary.getTitle());
            tvLocations.setText(itinerary.getLocations());
            // TODO - temporary distance - change once done w algo
            tvDistance.setText(itinerary.getTotalDistance());
            ParseFile image = itinerary.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivPhoto);
            }
        }

        private void queryDetail(Itinerary itinerary) {
            String detailsId = itinerary.getDetails().getObjectId();
            ParseQuery<Details> query = ParseQuery.getQuery(Details.class);
            // only get Detail with specific id
            query.whereEqualTo("objectId", detailsId);
            query.findInBackground(new FindCallback<Details>() {
                @Override
                public void done(List<Details> details, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Issue with getting details", e);
                        return;
                    }
                    if (details.size() != 1) {
                        Log.e(TAG, "We only want one detail!");
                        return;
                    }
                    Fragment fragment = new DetailsFragment(details.get(0), itinerary);
                    AppCompatActivity activity = (AppCompatActivity) context;
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentHome, fragment).addToBackStack(null).commit();
                }
            });
        }
    }
}
