package com.example.morim.database.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.morim.model.User;

@Dao
public interface CurrentUserDao {

    @Query("SELECT * from current_user where id = :currentUserId")
    LiveData<User> listenCurrentUser(String currentUserId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User value);


    @Query("DELETE from current_user")
    void delete();

}
