package com.example.morim.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.morim.database.OnDataCallback;
import com.example.morim.database.common.CurrentUserRepository;
import com.example.morim.database.common.UserRepository;
import com.example.morim.dto.UserLoginForm;
import com.example.morim.dto.UserRegisterForm;
import com.example.morim.model.Teacher;
import com.example.morim.model.User;
import com.example.morim.util.FirebaseListener;
import com.example.morim.util.LoadingState;
import com.google.firebase.auth.AuthResult;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final CurrentUserRepository currentUserRepository;

    private final MutableLiveData<Exception> _exceptions = new MutableLiveData<>();
    private final MutableLiveData<LoadingState> _loadingState = new MutableLiveData<>(LoadingState.Loaded);

    @Inject
    public AuthViewModel(UserRepository userRepository,
                         CurrentUserRepository currentUserRepository) {
        this.userRepository = userRepository;
        this.currentUserRepository = currentUserRepository;
        currentUserRepository.startListening();
    }

    public LiveData<Exception> getExceptions() {
        return _exceptions;
    }
    public LiveData<LoadingState> getLoadingState() {
        return _loadingState;
    }
    public LiveData<User> getCurrentUser() {
        return currentUserRepository.getCurrentUser();
    }

    public void createUser(UserRegisterForm form,OnDataCallback<User> callback) {

        _loadingState.postValue(LoadingState.Loading);
        userRepository.createNewUser(form, new OnDataCallback<User>() {
            @Override
            public void onData(User value) {
                _loadingState.postValue(LoadingState.Loaded);
                callback.onData(value);
            }

            @Override
            public void onException(Exception e) {
                _loadingState.postValue(LoadingState.Loaded);
                _exceptions.postValue(e);
                callback.onException(e);
            }
        });
    }


    public void signIn(UserLoginForm form, OnDataCallback<AuthResult> callback) {
        userRepository.signIn(form, new OnDataCallback<AuthResult>() {
            @Override
            public void onData(AuthResult value) {
                _loadingState.postValue(LoadingState.Loaded);
                callback.onData(value);
            }

            @Override
            public void onException(Exception e) {
                _loadingState.postValue(LoadingState.Loaded);
                callback.onException(e);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        currentUserRepository.stopListening();
    }
}
