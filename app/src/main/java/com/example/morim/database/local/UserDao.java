package com.example.morim.database.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.morim.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * from current_user")
    LiveData<List<User>> listenAllUsers();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<User> value);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User value);

}
