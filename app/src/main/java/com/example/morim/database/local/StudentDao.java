package com.example.morim.database.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.morim.model.Student;

import java.util.List;

@Dao
public interface StudentDao {

    @Query("SELECT * from students")
    LiveData<List<Student>> listenAllStudents();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Student> value);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Student value);

    @Query("UPDATE students SET comments = :commentsJson WHERE id = :studentId")
    void updateComments(String studentId, String commentsJson);
}
