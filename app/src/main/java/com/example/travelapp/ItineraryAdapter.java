package com.example.travelapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelapp.fragments.DetailsFragment;
import com.example.travelapp.fragments.HomeFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ViewHolder> {
    private static final String TAG = "ItineraryAdapter";
    private Context context;
    private List<Itinerary> itineraries;
    private DetailsLongClickListener detailsLongClickListener;

    public interface DetailsLongClickListener {
        void OnLongClick(int position);
    }

    public ItineraryAdapter(Context context, List<Itinerary> itineraries, DetailsLongClickListener detailsLongClickListener) {
        this.context = context;
        this.itineraries = itineraries;
        this.detailsLongClickListener = detailsLongClickListener;
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

    public void deleteItem(int position) {
        Itinerary deletedItinerary = itineraries.get(position);
        itineraries.remove(position);
        notifyItemRemoved(position);
        deleteInParse(deletedItinerary.getObjectId());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvLocations;
        private TextView tvDistance;
        private ImageView ivPhoto;
        private CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLocations = itemView.findViewById(R.id.tvLocations);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    detailsLongClickListener.OnLongClick(getAdapterPosition());
                    return true;
                }
            });
        }

        public void bind(Itinerary itinerary) {
            tvTitle.setText(itinerary.getTitle());
            tvLocations.setText(itinerary.getLocations());
            tvDistance.setText(itinerary.getTotalDistance());
            ParseFile image = itinerary.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivPhoto);
            }
        }
    }

    public void queryDetail(int position) {
        Itinerary itinerary = itineraries.get(position);
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

    public void queryMyDetail(int position) {
        Itinerary itinerary = itineraries.get(position);
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
                        .replace(R.id.fragmentProfile, fragment).addToBackStack(null).commit();
            }
        });
    }

    private void deleteInParse(String objectId) {
        ParseQuery<Itinerary> query = ParseQuery.getQuery(Itinerary.class);
        query.whereEqualTo("objectId", objectId);
        query.findInBackground(new FindCallback<Itinerary>() {
            @Override
            public void done(List<Itinerary> results, ParseException e) {
                if (e == null) {
                    if (e != null) {
                        Log.e(TAG, "Issue with getting itinerary to be deleted", e);
                        return;
                    }
                    if (results.size() != 1) {
                        Log.e(TAG, "We only should be deleting one itinerary!");
                        return;
                    }
                    results.get(0).deleteInBackground();
                }
            }
        });
    }

    // Clean all elements of the RV
    public void clear() {
        itineraries.clear();
        notifyDataSetChanged();
    }

    // Add a list of itineraries
    public void addAll(List<Itinerary> list) {
        itineraries.addAll(list);
        notifyDataSetChanged();
    }

}
