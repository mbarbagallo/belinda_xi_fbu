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
import com.example.travelapp.Details;
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
import com.google.maps.model.Distance;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.travelapp.Dijkstras.createGraph;
import static com.example.travelapp.Dijkstras.getShortestPath;

public class ComposeFragment extends Fragment {

    private static final String TAG = "ComposeFragment";
    public static final double METERS_IN_A_MILE = 1609.34;
    public static final String API_KEY = BuildConfig.apiKey;
    private Button btnAdd;
    private EditText etMoreLocations;
    private EditText etTitle;
    private Button btnSubmit;
    // list of location names
    private List<String> locations;
    // list of unique ids for each location
    private List<String> ids;
    private RecyclerView rvMoreItems;
    private MoreItemsAdapter moreItemsAdapter;
    public String placeName;
    public String placeId;
    private static final int firstPreference = 0;
    private static final int secondPreference = 1;

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
        this.etTitle = view.findViewById(R.id.etTitle);
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
                try {
                    Itinerary itinerary = saveItinerary(currentUser);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                saveDetails(currentUser, itinerary);
            }
        });

    }

    private Details saveDetails(ParseUser currentUser, Itinerary itinerary) throws InterruptedException {
        // create graph and call algorithm to determine path
        Graph graph = createGraph(ids, itinerary);
        // list that stores the indices of locations in order of traversal from locations list
        List<Integer> listNodesInt = getShortestPath(graph, firstPreference, secondPreference);
        // list that stores names of locations in order of traversal
        List<String> listNodesNames = new ArrayList<>();
        // list that stores distances between locations
        List<String> listDistances = new ArrayList<>();
        Double totalDistance = 0.0;
        GeoApiContext mGeoApiContext = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();
        for (int i = 0; i < listNodesInt.size(); i++) {
            // get node number at position i
            int nodeNumber = listNodesInt.get(i);
            if (i != listNodesInt.size() - 1) {
                // get distance between location i and i + 1
                Distance distance = itinerary.getDistance(ids.get(nodeNumber), ids.get(nodeNumber + 1), mGeoApiContext);
                // add distance String to listDistances
                String distanceMiles = distance.humanReadable;
                listDistances.add(distanceMiles);
                // convert distance to miles and add to totalDistance
                double distanceValue = Double.valueOf(distance.inMeters / METERS_IN_A_MILE);
                totalDistance += distanceValue;
            }
            // add location String name to listNodesNames
            listNodesNames.add(locations.get(nodeNumber));
        }
        // round totalDistance to 1 digit
        totalDistance = Math.round(totalDistance * 10) / 10.0;
        // save to Parse
        Details details = new Details();
        details.setDistances(listDistances);
        details.setDestinations(listNodesNames);
        itinerary.setTotalDistance(totalDistance.toString() + " mi");
        return details;
    }

    private Itinerary saveItinerary(ParseUser currentUser) throws InterruptedException {
        Itinerary itinerary = new Itinerary();
        GeoApiContext mGeoApiContext = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();
        itinerary.setLocations(locations);
        itinerary.setTitle(etTitle.getText().toString());
        // testing that getDistance works by getting distance between first two locations
        Distance distance = itinerary.getDistance(ids.get(0), ids.get(1), mGeoApiContext);
        itinerary.setUser(currentUser);
        itinerary.setIds(ids);
        // TODO - save details
        itinerary.setDetails(saveDetails(currentUser, itinerary));
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
                ids.clear();
                moreItemsAdapter.notifyDataSetChanged();
            }
        });
        return itinerary;
    }
}
