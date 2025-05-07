package com.example.morim;


import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import com.google.android.libraries.places.api.Places;
import com.google.common.collect.Lists;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

import java.util.List;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MorimApp extends Application {


    public static final String NOTIFICATION_CHANNEL = "not";

    public static List<String> ALL_SUBJECTS = subjects();

    private static List<String> subjects() {
        return Lists.newArrayList(
                "Select a subject",
                "Art & Drawing",
                "Computer science",
                "Cooking",
                "Economics",
                "Fitness & Wellness",
                "History",
                "Languages",
                "Law",
                "Math",
                "Music",
                "Philosophy",
                "Physical Sciences",
                "Programming",
                "Psychology",
                "Public Speaking",
                "Statistics"
        );
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());
        Places.initialize(getApplicationContext(), getString(R.string.MAPS_API_KEY));

        NotificationManager manager = getSystemService(NotificationManager.class);
        NotificationChannel channel =
                new NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_CHANNEL, NotificationManager.IMPORTANCE_HIGH);
        manager.createNotificationChannel(channel);
    }
}
