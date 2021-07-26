package com.example.travelapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.travelapp.Itinerary;
import com.example.travelapp.ItineraryAdapter;
import com.example.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvItineraries;
    protected List<Itinerary> allItineraries;
    protected ItineraryAdapter adapter;
    public static final String TAG = "HomeFragment";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvItineraries = view.findViewById(R.id.rvItineraries);

        allItineraries = new ArrayList<>();
        adapter = new ItineraryAdapter(getContext(), allItineraries);
        rvItineraries.setAdapter(adapter);
        rvItineraries.setLayoutManager(new LinearLayoutManager(getContext()));
        queryItineraries();
    }

    private void queryItineraries() {
        ParseQuery<Itinerary> query = ParseQuery.getQuery(Itinerary.class);
        query.include(Itinerary.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Itinerary>() {
            @Override
            public void done(List<Itinerary> itineraries, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                allItineraries.addAll(itineraries);
                adapter.notifyDataSetChanged();
            }
        });
    }
}