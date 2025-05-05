package com.example.morim.di;


import android.content.SharedPreferences;

import com.example.morim.database.common.ChatRepository;
import com.example.morim.database.common.FavoritesRepository;
import com.example.morim.database.common.MeetingRepository;
import com.example.morim.database.local.AppDatabase;
import com.example.morim.database.local.ChatDao;
import com.example.morim.database.local.FavoritesDao;
import com.example.morim.database.local.MeetingDao;
import com.example.morim.database.remote.FirebaseChatManager;
import com.example.morim.database.remote.FirebaseFavoritesManager;
import com.example.morim.database.remote.FirebaseMeetingsManager;
import com.example.morim.util.FirebaseUtil;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class FavoritesModule {

    @Provides
    @Singleton
    public FavoritesRepository provideFavoritesRepository(
            ScheduledThreadPoolExecutor executor,
            FavoritesDao favoritesDao,
            FirebaseFavoritesManager remoteDb
    ) {
        return new FavoritesRepository(favoritesDao, remoteDb, executor);
    }

    @Provides
    @Singleton
    public FirebaseFavoritesManager provideRemoteFavoritesManager(SharedPreferences sp, FirebaseUtil firebaseUtil) {
        return new FirebaseFavoritesManager(firebaseUtil, sp);
    }

    @Provides
    @Singleton
    public FavoritesDao provideFavoritesDao(AppDatabase appDatabase) {
        return appDatabase.favoritesDao();
    }

}
