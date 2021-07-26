package com.example.travelapp;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel(analyze = Details.class)
@ParseClassName("Details")
public class Details extends ParseObject {

    public static final String KEY_TOTAL_DISTANCE = "totalDistance";
    public static final String KEY_DESTINATIONS = "destinations";
    private static final String KEY_DISTANCES = "distances";
    public static final String KEY_USER = "user";
    private static final String TAG = "Details";

    public void setTotalDistance(String distance) {
        put(KEY_TOTAL_DISTANCE, distance);
    }

    public void setDestinations(List<String> destinations) {
        put(KEY_DESTINATIONS, destinations);
    }

    public void setDistances(List<String> distances) {
        put(KEY_DISTANCES, distances);
    }

    public List<String> getDestinations() {
        JSONArray destinationsJSONArray = getJSONArray(KEY_DESTINATIONS);
        List<String> destinationsList = new ArrayList<>();
        for (int i = 0; i < destinationsJSONArray.length(); i++) {
            try {
                destinationsList.add(destinationsJSONArray.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return destinationsList;
    }

    public List<String> getDistances() {
        JSONArray distancesJSONArray = getJSONArray(KEY_DISTANCES);
        List<String> distancesList = new ArrayList<>();
        for (int i = 0; i < distancesJSONArray.length(); i++) {
            try {
                distancesList.add(distancesJSONArray.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return distancesList;
    }

    public String getTotalDistance() {
        return getString(KEY_TOTAL_DISTANCE);
    }
}
