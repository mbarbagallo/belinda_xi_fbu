package com.example.travelapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private RecyclerView rvMyItineraries;
    protected List<Itinerary> allMyItineraries;
    protected ItineraryAdapter adapter;
    public static final String TAG = "ProfileFragment";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // setting up recycler view for just the current user's itineraries
        rvMyItineraries = view.findViewById(R.id.rvMyItineraries);
        allMyItineraries = new ArrayList<>();
        adapter = new ItineraryAdapter(getContext(), allMyItineraries);
        rvMyItineraries.setAdapter(adapter);
        rvMyItineraries.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new HomeFragment.SwipeToDelete(adapter));
        itemTouchHelper.attachToRecyclerView(rvMyItineraries);
        queryMyItineraries();
    }

    private void queryMyItineraries() {
        ParseQuery<Itinerary> query = ParseQuery.getQuery(Itinerary.class);
        // get current user and only get itineraries from the current user
        ParseUser user = ParseUser.getCurrentUser();
        query.whereEqualTo("user", user);
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
                allMyItineraries.addAll(itineraries);
                adapter.notifyDataSetChanged();
            }
        });
    }
}