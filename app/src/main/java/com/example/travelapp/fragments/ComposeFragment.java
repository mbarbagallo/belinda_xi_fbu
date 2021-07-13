package com.example.travelapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.travelapp.Itinerary;
import com.example.travelapp.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ComposeFragment extends Fragment {

    private static final String TAG = "ComposeFragment";
    private Button btnAdd;
    private EditText etMoreLocations;
    private Button btnSubmit;
    private EditText etLocation1;
    private EditText etLocation2;
    private List<String> locations;

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
        this.etMoreLocations = view.findViewById(R.id.etMoreLocations);
        this.etLocation1 = view.findViewById(R.id.etLocation1);
        this.etLocation2 = view.findViewById(R.id.etLocation2);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - add more locations and display on RV
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add required 2 locations to list of locations
                String location1 = etLocation1.getText().toString();
                String location2 = etLocation2.getText().toString();
                // check for locations being empty
                if (location1.isEmpty() || location2.isEmpty()) {
                    Toast.makeText(getContext(), "must enter at least 2 locations!", Toast.LENGTH_SHORT).show();
                    return;
                }
                locations = new ArrayList<>();
                locations.add(location1);
                locations.add(location2);
                // TODO - add additional locations to list of locations

                ParseUser currentUser = ParseUser.getCurrentUser();
                saveItinerary(locations, currentUser);
            }
        });
    }
    private void saveItinerary(List<String> locations, ParseUser currentUser) {
        Itinerary itinerary = new Itinerary();
        itinerary.setLocations(locations);
        itinerary.setUser(currentUser);
        itinerary.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error while saving", e);
                    Toast.makeText(getContext(), "error while saving!", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getContext(), "post save was successful!", Toast.LENGTH_SHORT).show();
                etLocation1.setText("");
                etLocation2.setText("");
                etMoreLocations.setText("");
            }
        });
    }
}
