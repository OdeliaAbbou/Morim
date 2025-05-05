package com.example.morim.di;


import android.content.SharedPreferences;

import com.example.morim.database.common.ChatRepository;
import com.example.morim.database.common.MeetingRepository;
import com.example.morim.database.common.UserRepository;
import com.example.morim.database.local.AppDatabase;
import com.example.morim.database.local.ChatDao;
import com.example.morim.database.local.CurrentUserDao;
import com.example.morim.database.local.MeetingDao;
import com.example.morim.database.local.StudentDao;
import com.example.morim.database.local.TeacherDao;
import com.example.morim.database.remote.FirebaseChatManager;
import com.example.morim.database.remote.FirebaseMeetingsManager;
import com.example.morim.database.remote.FirebaseUserManager;
import com.example.morim.util.FirebaseUtil;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class MeetingModule {

    @Provides
    @Singleton
    public MeetingRepository provideMeetingRepository(
            ScheduledThreadPoolExecutor executor,
            MeetingDao meetingDao,
            FirebaseMeetingsManager remoteDb
    ) {
        return new MeetingRepository(executor, meetingDao, remoteDb);
    }

    @Provides
    @Singleton
    public ChatRepository provideChatRepository(
            ScheduledThreadPoolExecutor executor,
            ChatDao chatDao,
            FirebaseChatManager remoteDb
    ) {
        return new ChatRepository(executor, chatDao, remoteDb);
    }


    @Provides
    @Singleton
    public FirebaseChatManager provideRemoteChatManager(FirebaseUtil firebaseUtil, SharedPreferences sp) {
        return new FirebaseChatManager(firebaseUtil, sp);
    }

    @Provides
    @Singleton
    public FirebaseMeetingsManager provideRemoteMeetingsManager(FirebaseUtil firebaseUtil, SharedPreferences sp) {
        return new FirebaseMeetingsManager(sp, firebaseUtil);
    }

    @Provides
    @Singleton
    public MeetingDao provideMeetingsDao(AppDatabase appDatabase) {
        return appDatabase.meetingDao();
    }

    @Provides
    @Singleton
    public ChatDao provideChatsDao(AppDatabase appDatabase) {
        return appDatabase.chatDao();
    }

}
