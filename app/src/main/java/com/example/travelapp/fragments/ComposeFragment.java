package com.example.travelapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.BuildConfig;
import com.example.travelapp.Itinerary;
import com.example.travelapp.MoreItemsAdapter;
import com.example.travelapp.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.GeoApiContext;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComposeFragment extends Fragment {

    private static final String TAG = "ComposeFragment";
    public static final String API_KEY = BuildConfig.apiKey;
    private Button btnAdd;
    private EditText etMoreLocations;
    private Button btnSubmit;
    // list of location names
    private List<String> locations;
    // list of unique ids for each location
    private List<String> ids;
    private RecyclerView rvMoreItems;
    private MoreItemsAdapter moreItemsAdapter;
    public String placeName;
    public String placeId;

    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    // set up views and listeners
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.btnAdd = view.findViewById(R.id.btnAdd);
        this.btnSubmit = view.findViewById(R.id.btnSubmit);
        this.rvMoreItems = view.findViewById(R.id.rvMoreItems);
        locations = new ArrayList<>();
        ids = new ArrayList<>();

        moreItemsAdapter = new MoreItemsAdapter(getContext(), locations);
        rvMoreItems.setAdapter(moreItemsAdapter);
        rvMoreItems.setLayoutManager(new LinearLayoutManager(getContext()));

        Places.initialize(getContext(), API_KEY);
        PlacesClient placesClient = Places.createClient(getContext());
        AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) getChildFragmentManager()
                        .findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autocompleteSupportFragment.setCountry("US");
        // TODO - possibly add more fields
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                placeName = place.getName();
                placeId = place.getId();
                Log.i(TAG, "place: " + placeName + " id: " + place.getId());
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.e(TAG, "error occurred: " + status);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (placeName.isEmpty()) {
                    Toast.makeText(getContext(), "can't enter empty location!", Toast.LENGTH_SHORT).show();
                    return;
                }
                locations.add(placeName);
                ids.add(placeId);
                moreItemsAdapter.notifyItemInserted(locations.size() - 1);
                autocompleteSupportFragment.setText("");
                Toast.makeText(getContext(), "location was added", Toast.LENGTH_SHORT).show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check for too few locations
                if (locations.size() < 2){
                    Toast.makeText(getContext(), "must enter at least 2 locations!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                saveItinerary(currentUser);
            }
        });

    }
    private void saveItinerary(ParseUser currentUser) {
        Itinerary itinerary = new Itinerary();
        GeoApiContext mGeoApiContext = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();
        itinerary.setLocations(locations);
        Log.i("from " + locations.get(0), " to: " + locations.get(1));
        // testing that getDistance works by getting distance between first two locations
        itinerary.getDistance(ids.get(0), ids.get(1), mGeoApiContext);
        itinerary.setUser(currentUser);
        itinerary.setIds(ids);
        itinerary.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error while saving", e);
                    Toast.makeText(getContext(), "error while saving!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getContext(), "post save was successful!", Toast.LENGTH_SHORT).show();
                locations.clear();
                moreItemsAdapter.notifyDataSetChanged();
            }
        });
    }
}
