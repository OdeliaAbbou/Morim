package com.example.morim.di;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.morim.database.common.CurrentUserRepository;
import com.example.morim.database.common.UserRepository;
import com.example.morim.database.local.AppDatabase;
import com.example.morim.database.local.CurrentUserDao;
import com.example.morim.database.local.StudentDao;
import com.example.morim.database.local.TeacherDao;
import com.example.morim.database.remote.FirebaseUserManager;
import com.example.morim.model.User;
import com.example.morim.util.FirebaseUtil;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import im.delight.android.location.SimpleLocation;

@Module
@InstallIn(SingletonComponent.class)
public class MorimModule {
    @Provides
    @Singleton
    public SimpleLocation provideLocationService(@ApplicationContext Context context) {
        return new SimpleLocation(context);
    }
    @Provides
    @Singleton
    public SharedPreferences provideSharedPrefs(@ApplicationContext Context context) {
        return context.getSharedPreferences("com.example.morim", Context.MODE_PRIVATE);
    }
    @Provides
    public ScheduledThreadPoolExecutor provideScheduler() {
        return new ScheduledThreadPoolExecutor(4);
    }

    @Provides
    @Singleton
    public AppDatabase provideLocalDB(@ApplicationContext Context context) {
        return AppDatabase.getInstance(context);
    }

    @Provides
    @Singleton
    public FirebaseUtil provideFirebaseUtil(SharedPreferences sp) {
        return new FirebaseUtil(sp);
    }


}
