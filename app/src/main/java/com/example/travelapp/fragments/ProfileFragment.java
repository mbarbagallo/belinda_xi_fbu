package com.example.travelapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.travelapp.Itinerary;
import com.example.travelapp.ItineraryAdapter;
import com.example.travelapp.LogInActivity;
import com.example.travelapp.MainActivity;
import com.example.travelapp.R;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends Fragment {

    private RecyclerView rvItineraries;
    protected List<Itinerary> allItineraries;
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
    }
}