package com.example.morim.database.local;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.morim.model.Meeting;

import java.util.List;

@Dao
public interface MeetingDao {
    @Query("SELECT * from my_meetings")
    LiveData<List<Meeting>> listenMyMeetings();

    @Query("SELECT * from my_meetings")
    List<Meeting> getMyMeetings();


    @Query("DELETE FROM my_meetings")
    int deleteAllMeetings();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Meeting> value);
    @Delete
    void delete(List<Meeting> value);
    @Delete
    void delete(Meeting value);

    @Query("DELETE FROM my_meetings")
    void removeAll();
}
