package com.example.morim.database.local;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.morim.model.Chat;
import com.example.morim.model.Favorite;
import com.example.morim.model.Meeting;

import com.example.morim.model.Student;
import com.example.morim.model.Teacher;
import com.example.morim.model.User;

@Database(entities = {Teacher.class, Student.class, User.class, Meeting.class, Chat.class, Favorite.class}, version = 19)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract StudentDao studentDao();

    public abstract TeacherDao teacherDao();

    public abstract MeetingDao meetingDao();
    public abstract FavoritesDao favoritesDao();
    public abstract ChatDao chatDao();

    public abstract CurrentUserDao currentUserDao();
    public abstract UserDao userDao();

    public static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context, AppDatabase.class, "another_db")
                    .fallbackToDestructiveMigration()
                    .build();
        return instance;
    }


}
