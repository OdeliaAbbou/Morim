package com.example.morim.database.local;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.morim.model.Teacher;
import com.example.morim.model.User;

import java.util.List;

@Dao
public interface TeacherDao {
    @Query("SELECT * from teachers")
    LiveData<List<Teacher>> listenAllTeachers();
    @Query("SELECT * from teachers where id = :id")
    LiveData<Teacher> listenTeacher(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Teacher> value);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Teacher value);


    @Update
    void update(Teacher teacher);

    @Query("UPDATE teachers SET comments = :commentsJson WHERE id = :teacherId")
    void updateComments(String teacherId, String commentsJson);
}
