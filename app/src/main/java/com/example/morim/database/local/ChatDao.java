package com.example.morim.database.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.morim.model.Chat;
import com.example.morim.model.User;

import java.util.List;

@Dao
public interface ChatDao {

    @Query("SELECT * from chats")
    LiveData<List<Chat>> listenAllChats();

    @Query("SELECT * from chats where id = :id LIMIT 1")
    LiveData<Chat> listenChat(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Chat value);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Chat> value);

    @Query("DELETE from chats where teacherId = :teacherId and studentId = :studentId")
    void deleteChat(String studentId, String teacherId);

    @Query("DELETE from chats")
    void delete();

    @Query("DELETE from chats")
    void removeAll();
}
