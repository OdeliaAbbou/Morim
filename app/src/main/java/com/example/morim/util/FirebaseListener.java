package com.example.morim.util;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.ListenerRegistration;

public class FirebaseListener<T> {

    private final LiveData<T> liveData;
    private final ListenerRegistration listenerRegistration;

    public FirebaseListener(LiveData<T> liveData, ListenerRegistration registration) {
        this.liveData = liveData;
        this.listenerRegistration = registration;
    }

    public LiveData<T> get() {
        return liveData;
    }

    public void stopListening() {
        listenerRegistration.remove();
    }
}
