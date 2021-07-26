package com.example.travelapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Register parse models here - ParseObject.registerSubclass(ClassName.class);
        ParseObject.registerSubclass(Itinerary.class);
        ParseObject.registerSubclass(Details.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("AxIxYefyJfSCMZBUvkf9C6mAMws6kcZ1rwaquh3L")
                .clientKey("BsmUUhD7JEH5tivoat5nnAANOYvglYpKok0ysmx9")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
