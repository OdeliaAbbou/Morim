package com.example.morim.database.common;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.morim.database.OnDataCallback;
import com.example.morim.database.local.CurrentUserDao;
import com.example.morim.database.local.MeetingDao;
import com.example.morim.model.User;
import com.example.morim.util.FirebaseListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class CurrentUserRepository {

    private final ScheduledThreadPoolExecutor executor;
    private final CurrentUserDao currentUserDao;
    private FirebaseListener<User> currentUser;
    private final MediatorLiveData<User> currentUserMediator;

    private final UserRepository userRepository;
    private final MeetingDao meetingsDao;

    private FirebaseAuth.AuthStateListener authStateListener;

    public CurrentUserRepository(
            ScheduledThreadPoolExecutor executor,
            MeetingDao meetingsDao,
            CurrentUserDao currentUserDao,
            UserRepository userRepository) {
        this.executor = executor;
        this.meetingsDao = meetingsDao;
        this.userRepository = userRepository;
        this.currentUserDao = currentUserDao;
        currentUserMediator = new MediatorLiveData<>();
    }

    public LiveData<User> getCurrentUser() {
        return currentUserMediator;
    }

    public void startListening() {
        authStateListener = (authState) -> {
            if (authState.getCurrentUser() == null) {
                executor.execute(() -> {
                    currentUserDao.delete();
                    meetingsDao.deleteAllMeetings();
                });
            } else {
                currentUser = userRepository.listenCurrentUser(unused -> currentUserMediator.postValue(null));
                currentUserMediator.addSource(currentUser.get(), currentUserMediator::postValue);
            }
        };
        FirebaseAuth.getInstance()
                .addAuthStateListener(authStateListener);
    }

    public void signOut(OnSuccessListener<Void> onSuccessListener) {
        executor.execute(() -> {
            FirebaseAuth.getInstance()
                    .signOut();
            currentUserDao.delete();
            currentUserMediator.postValue(null);
            onSuccessListener.onSuccess(null);
        });
    }

    public void stopListening() {
        if (authStateListener != null) {
            FirebaseAuth.getInstance()
                    .removeAuthStateListener(authStateListener);
        }
        if (currentUser != null) {
            currentUser.stopListening();
            currentUserMediator.removeSource(currentUser.get());
        }
    }
}
