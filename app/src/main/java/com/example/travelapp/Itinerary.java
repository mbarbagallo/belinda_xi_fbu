package com.example.travelapp;

import android.util.Log;

import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.Distance;
import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;

import java.util.List;

@Parcel(analyze = Itinerary.class)
@ParseClassName("Itinerary")
public class Itinerary extends ParseObject{

    public static final String KEY_LOCATIONS = "locations";
    public static final String KEY_IDS = "ids";
    public static final String KEY_USER = "user";
    private static final String TAG = "Itinerary";
    private static final String KEY_TITLE = "title";
    private Distance distance = new Distance();

    public String getLocations() {
        JSONArray locationsArray = getJSONArray(KEY_LOCATIONS);
        String locations = null;
        for (int i = 0; i < locationsArray.length(); i++) {
            try {
                if (i == 0) {
                    locations = locationsArray.getString(0);
                } else {
                    locations = locations + ", " + locationsArray.getString(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "itinerary locations: " + locations);
        return locations;
    }

    public void setLocations(List<String> locations) {
        put(KEY_LOCATIONS, locations);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {
        put(KEY_USER, parseUser);
    }

    public void setIds(List<String> ids) {
        put(KEY_IDS, ids);
    }

    public Distance getDistance(String id1, String id2, GeoApiContext mGeoApiContext) {

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.origin("place_id:" + id1);
        directions.destination("place_id:" + id2).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                // TODO - for possibly considering routes & duration too later
//                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
//                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                distance = result.routes[0].legs[0].distance;
            }
            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );
            }
        });
        return distance;
    }

    public ParseFile getImage() {
        // TODO - add image to Itinerary Parse Object and return it here
        return null;
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }
}
