package com.example.morim.database.remote;

import android.content.SharedPreferences;

import com.example.morim.database.OnDataCallback;
import com.example.morim.model.Favorite;
import com.example.morim.util.FirebaseUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class FirebaseFavoritesManager {
    private final SharedPreferences sp;

    public final String FAVORITES_COLLECTION = "favorites";
    public final String USERS_COLLECTION = "users";
    private final FirebaseUtil firebaseUtil;

    public FirebaseFavoritesManager(FirebaseUtil firebaseUtil, SharedPreferences sharedPreferences) {
        this.firebaseUtil = firebaseUtil;
        this.sp = sharedPreferences;
    }

    public Task<Favorite> addFavorite(String favoriteId) {
        Favorite f = new Favorite(favoriteId);
        return firebaseUtil.insertDocDesignatedId(
                USERS_COLLECTION,
                FAVORITES_COLLECTION,
                FirebaseAuth.getInstance().getUid(),
                favoriteId,
                f
        ).continueWith(task -> f);
    }

    public Task<Void> removeFavorite(String favoriteId) {
        return firebaseUtil.deleteDoc(
                USERS_COLLECTION,
                FAVORITES_COLLECTION,
                FirebaseAuth.getInstance().getUid(),
                favoriteId
        );
    }

    public ListenerRegistration listenFavorites(OnDataCallback<List<Favorite>> callback) {
        return firebaseUtil.listenDocs(
                USERS_COLLECTION,
                FAVORITES_COLLECTION,
                FirebaseAuth.getInstance().getUid(),
                callback,
                Favorite.class
        );
    }

    public void getFavorites(OnDataCallback<List<Favorite>> callback) {
        firebaseUtil.getDocs(
                USERS_COLLECTION,
                FAVORITES_COLLECTION,
                FirebaseAuth.getInstance().getUid(),
                callback,
                Favorite.class
        );
    }
}
