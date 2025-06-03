package com.example.morim.di;

import android.content.SharedPreferences;

import com.example.morim.database.common.CurrentUserRepository;
import com.example.morim.database.common.UserRepository;
import com.example.morim.database.local.AppDatabase;
import com.example.morim.database.local.CurrentUserDao;
import com.example.morim.database.local.MeetingDao;
import com.example.morim.database.local.StudentDao;
import com.example.morim.database.local.TeacherDao;
import com.example.morim.database.local.UserDao;
import com.example.morim.database.remote.FirebaseUserManager;
import com.example.morim.util.FirebaseUtil;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class UserModule {

    @Provides
    @Singleton
    public FirebaseUserManager provideRemoteUserManager(FirebaseUtil firebaseUtil, SharedPreferences sp) {
        return new FirebaseUserManager(firebaseUtil, sp);
    }

    @Provides
    @Singleton
    public TeacherDao provideTeacherDao(AppDatabase appDatabase) {
        return appDatabase.teacherDao();
    }

    @Provides
    @Singleton
    public StudentDao provideStudentDao(AppDatabase appDatabase) {
        return appDatabase.studentDao();
    }

    @Provides
    @Singleton
    public CurrentUserDao provideCurrentUserDao(AppDatabase appDatabase) {
        return appDatabase.currentUserDao();
    }

    @Provides
    @Singleton
    public UserDao provideUserDao(AppDatabase appDatabase) {
        return appDatabase.userDao();
    }



    @Provides
    @Singleton
    public UserRepository provideUserRepository(
            SharedPreferences sp,
            ScheduledThreadPoolExecutor executor,
            StudentDao studentDao,
            TeacherDao teacherDao,
            CurrentUserDao currentUserDao,
            UserDao userDao,
            FirebaseUserManager remoteDb
            ) {
        return new UserRepository(sp, executor, studentDao, teacherDao, currentUserDao, userDao, remoteDb);
    }


    @Provides
    @Singleton
    public CurrentUserRepository provideCurrentUserRepository(
            ScheduledThreadPoolExecutor executor,
            CurrentUserDao currentUserDao,
            MeetingDao meetingDao,
            UserRepository userRepository) {
        return new CurrentUserRepository(executor, meetingDao, currentUserDao, userRepository);
    }


}
