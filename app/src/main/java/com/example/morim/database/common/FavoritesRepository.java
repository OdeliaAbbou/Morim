package com.example.morim.database.common;

import com.example.morim.database.OnDataCallback;
import com.example.morim.database.local.FavoritesDao;
import com.example.morim.database.remote.FirebaseFavoritesManager;
import com.example.morim.model.Favorite;
import com.example.morim.util.FirebaseListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class FavoritesRepository {

    private final FavoritesDao favoritesDao;
    private final FirebaseFavoritesManager remoteDb;

    private final ScheduledThreadPoolExecutor executor;


    public FavoritesRepository(FavoritesDao favoritesDao, FirebaseFavoritesManager remoteDb, ScheduledThreadPoolExecutor executor) {
        this.favoritesDao = favoritesDao;
        this.remoteDb = remoteDb;
        this.executor = executor;
    }

    public Task<Favorite> addFavorite(String favoriteId) {
        return remoteDb.addFavorite(favoriteId)
                .continueWith((favorite) -> {
                    if (favorite.isSuccessful()) {
                        Favorite f = favorite.getResult();
                        executor.execute(() -> {
                            favoritesDao.insert(f);
                        });
                        return f;
                    }
                    throw new RuntimeException("Failed to add favorite");
                });
    }

    public Task<Void> removeFavorite(String favoriteId) {
        return remoteDb.removeFavorite(favoriteId)
                .continueWith((favorite) -> {
                    if (favorite.isSuccessful()) {
                        executor.execute(() -> {
                            favoritesDao.deleteFavorite(favoriteId);
                        });
                        return null;
                    }
                    throw new RuntimeException("Failed to remove favorite");
                });
    }

    public void getFavorites(OnDataCallback<List<Favorite>> callback) {
        remoteDb.getFavorites(callback);
    }
    public FirebaseListener<List<Favorite>> listenFavorites() {
        return new FirebaseListener<>(
                favoritesDao.listenAllFavorites(),
                remoteDb.listenFavorites(new OnDataCallback<List<Favorite>>() {
                    @Override
                    public void onData(List<Favorite> value) {
                        executor.execute(() -> {
                            favoritesDao.removeAll();
                            favoritesDao.insertAll(value);
                        });
                    }
                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
                    }
                }));
    }

}
