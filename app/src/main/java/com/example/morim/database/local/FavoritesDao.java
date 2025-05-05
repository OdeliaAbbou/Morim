package com.example.morim.database.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.morim.model.Chat;
import com.example.morim.model.Favorite;

import java.util.List;

@Dao
public interface FavoritesDao {

    @Query("SELECT * from favorites")
    LiveData<List<Favorite>> listenAllFavorites();

    @Query("SELECT * from favorites where id = :id LIMIT 1")
    LiveData<Favorite> listenFavorite(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Favorite value);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Favorite> values);

    @Query("DELETE from favorites where id = :id")
    void deleteFavorite(String id);

    @Query("DELETE from favorites")
    void delete();

    @Query("DELETE from favorites")
    void removeAll();
}
