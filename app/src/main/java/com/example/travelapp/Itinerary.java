package com.example.travelapp;

import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.List;

@Parcel(analyze = Itinerary.class)
@ParseClassName("Itinerary")
public class Itinerary extends ParseObject{

    public static final String KEY_LOCATIONS = "locations";
    public static final String KEY_IDS = "ids";
    public static final String KEY_USER = "user";

    public String getLocations() {
        return getString(KEY_LOCATIONS);
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
}
