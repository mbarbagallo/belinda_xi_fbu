package com.example.travelapp.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.travelapp.Details;
import com.example.travelapp.DetailsAdapter;
import com.example.travelapp.Itinerary;
import com.example.travelapp.MainActivity;
import com.example.travelapp.R;
import com.parse.ParseFile;

import org.w3c.dom.Text;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class DetailsFragment extends Fragment {

    private TextView tvTitleDetails;
    private TextView tvDestinationsDetails;
    private TextView tvDistanceDetails;
    private ImageView ivPhotoDetails;
    private RecyclerView rvDetails;
    private TextView tvUserDetails;
    private Details details;
    private Itinerary itinerary;
    private DetailsAdapter adapter;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public DetailsFragment(Details details, Itinerary itinerary) {
        this.details = details;
        this.itinerary = itinerary;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) getActivity())
                .getSupportActionBar().setTitle("Trip Details");
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitleDetails = view.findViewById(R.id.tvTitle);
        tvDestinationsDetails = view.findViewById(R.id.tvDestinationsDetails);
        tvDistanceDetails = view.findViewById(R.id.tvDistance);
        ivPhotoDetails = view.findViewById(R.id.ivPhoto);
        tvUserDetails = view.findViewById(R.id.tvUserDetails);

        tvTitleDetails.setText(itinerary.getTitle());
        // convert array to String and remove [] at both ends of the String
        String destinations = details.getDestinations().toString();
        destinations = destinations.substring(1, destinations.length() - 1);

        tvDestinationsDetails.setText(destinations);
        tvDistanceDetails.setText(itinerary.getTotalDistance());
        tvUserDetails.setText("@" + itinerary.getUser().getUsername());
        
        rvDetails = view.findViewById(R.id.rvDetails);
        adapter = new DetailsAdapter(getContext(), details);
        rvDetails.setAdapter(adapter);
        rvDetails.setLayoutManager(new LinearLayoutManager(getContext()));

        ParseFile image = itinerary.getImage();
        if (image != null) {
            Glide.with(getContext()).load(image.getUrl()).into(ivPhotoDetails);
        }
    }
}
