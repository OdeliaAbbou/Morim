package com.example.morim.viewmodel;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.morim.database.OnDataCallback;
import com.example.morim.database.common.CurrentUserRepository;
import com.example.morim.database.common.FavoritesRepository;
import com.example.morim.database.common.MeetingRepository;
import com.example.morim.database.common.UserRepository;
import com.example.morim.model.Favorite;
import com.example.morim.model.Meeting;
import com.example.morim.model.MyMeetingsData;
import com.example.morim.model.Student;
import com.example.morim.model.Teacher;
import com.example.morim.model.User;
import com.example.morim.ui.dialog.ScheduleMeetingDialog;
import com.example.morim.ui.dialog.SubjectDialog;
import com.example.morim.util.DateUtils;
import com.example.morim.util.FirebaseListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {


    private MutableLiveData<Exception> exceptions;
    private final UserRepository userRepository;

    private final MeetingRepository meetingRepository;
    private final CurrentUserRepository currentUserRepository;
    private final FavoritesRepository favoritesRepository;

    private FirebaseListener<List<Teacher>> teachers;
    private FirebaseListener<List<Meeting>> myMeetings;

    private FirebaseListener<List<Favorite>> myFavorites;

    private final MutableLiveData<List<Teacher>> teachersSearchResults = new MutableLiveData<>(new ArrayList<>());


    private HashMap<String, List<Meeting>> cachedMeetings = new HashMap<>();

    private final FirebaseListener<List<User>> users;


    private final MediatorLiveData<MyMeetingsData> myMeetingsDataMediatorLiveData = new MediatorLiveData<>();

    @Inject
    public MainViewModel(UserRepository userRepository,
                         MeetingRepository meetingRepository,
                         CurrentUserRepository currentUserRepository,
                         FavoritesRepository favoritesRepository) {
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.currentUserRepository = currentUserRepository;
        this.favoritesRepository = favoritesRepository;
        currentUserRepository.startListening();
        users = userRepository.listenUsers();
        myMeetings = meetingRepository.listenMyMeetings();
        teachers = userRepository.listenTeachers();
        myFavorites = favoritesRepository.listenFavorites();

        MyMeetingsData combinedData = new MyMeetingsData();
        myMeetingsDataMediatorLiveData.addSource(users.get(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                combinedData.setUsers(users);
                myMeetingsDataMediatorLiveData.postValue(combinedData);
            }
        });

        myMeetingsDataMediatorLiveData.addSource(myMeetings.get(), new Observer<List<Meeting>>() {
            @Override
            public void onChanged(List<Meeting> meetings) {
                combinedData.setMyMeetings(meetings);
                myMeetingsDataMediatorLiveData.postValue(combinedData);
            }
        });
    }

    public LiveData<List<Favorite>> getMyFavorites() {
        return myFavorites.get();
    }

    public void getMyFavoritesOnce(OnDataCallback<List<Favorite>> callback) {
        favoritesRepository.getFavorites(callback);
    }
    public LiveData<MyMeetingsData> getMyMeetingsData() {
        return myMeetingsDataMediatorLiveData;
    }

    public LiveData<List<Teacher>> getTeachers() {
        return teachers.get();
    }


    public LiveData<List<Teacher>> getTeacherSearchResults() {
        return teachersSearchResults;
    }

    public Task<Void> scheduleMeeting(Meeting meeting) {
        return meetingRepository.scheduleMeeting(meeting)
                .addOnSuccessListener(unused -> cachedMeetings.computeIfAbsent(meeting.getTeacherId(), k -> new ArrayList<>()).add(meeting));
    }

    public Task<Void> cancelMeeting(Meeting meeting) {
        return meetingRepository.cancelMeeting(meeting)
                .addOnSuccessListener(unused -> cachedMeetings.computeIfAbsent(meeting.getTeacherId(), k -> new ArrayList<>()).remove(meeting));
    }
    public Task<Favorite> addFavorite(String favoriteId) {
        return favoritesRepository.addFavorite(favoriteId);
    }

    public Task<Void> removeFavorite(String favoriteId) {
        return favoritesRepository.removeFavorite(favoriteId);
    }
    public void rateTeacher(Teacher teacher, double rating) {

        int ratings = teacher.getRatingStudents().size();
        double average = teacher.getAverageRating();
        double all = average * ratings;
        all += rating;
        all /= (ratings + 1);
        teacher.getRatingStudents().add(FirebaseAuth.getInstance().getUid());
        teacher.setAverageRating(all);
        teacher.setUpdatedAt(System.currentTimeMillis());
        userRepository.updateUser(teacher, null, new OnDataCallback<User>() {
            @Override
            public void onData(User value) {

            }

            @Override
            public void onException(Exception e) {
                exceptions.postValue(e);
            }
        });
    }


    public void filterTeachers(String subject) {
        List<Teacher> results = this.teachers.get().getValue();
        if (results == null) results = new ArrayList<>();
        results = results.stream()
                .filter(teacher -> teacher.getTeachingSubjects()
                        .contains(subject))
                .collect(Collectors.toList());
        teachersSearchResults.postValue(results);
    }

    public void filterTeachers(String subject, double minPrice, double maxPrice) {
        List<Teacher> results = this.teachers.get().getValue();
        if (results == null) results = new ArrayList<>();
        results = results.stream()
                .filter(teacher -> teacher.getTeachingSubjects().contains(subject))
                .filter(teacher -> teacher.getPrice() >= minPrice && teacher.getPrice() <= maxPrice)
                .collect(Collectors.toList());
        teachersSearchResults.postValue(results);
    }


    public void getTeacherMeetings(String tid, OnDataCallback<List<Meeting>> callback) {
        if (cachedMeetings.containsKey(tid)) {
            callback.onData(cachedMeetings.get(tid));
            return;
        }
        meetingRepository.getTeacherMeetings(tid, new OnDataCallback<List<Meeting>>() {
            @Override
            public void onData(List<Meeting> value) {
                cachedMeetings.put(tid, value);
                callback.onData(value);
            }

            @Override
            public void onException(Exception e) {
                exceptions.postValue(e);
                callback.onException(e);
            }
        });
    }

    public void showScheduleMeetingDialog(Context context,
                                          FragmentManager manager,
                                          Teacher teacher) {
        getTeacherMeetings(teacher.getId(), new OnDataCallback<List<Meeting>>() {
            @Override
            public void onData(List<Meeting> meetings) {
                ScheduleMeetingDialog meetingDialog = new ScheduleMeetingDialog(meetings, teacher, date -> {
                    long meetingDate = DateUtils.dateTimeToEpoch(date);
                    SubjectDialog.showSubjectDialog(context, subject -> {
                        String myId = FirebaseAuth.getInstance().getUid();
                        scheduleMeeting(new Meeting("", myId, teacher.getId(), meetingDate, subject
                        )).addOnSuccessListener(unused -> Toast.makeText(context, "Scheduled meeting with " + teacher.getFullName() + " !", Toast.LENGTH_LONG).show()).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed to schedule meeting with " + teacher.getFullName() + " !", Toast.LENGTH_LONG).show();
                            }
                        });
                    });
                });
                meetingDialog.show(manager, "Meeting dialog");
            }

            @Override
            public void onException(Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public LiveData<Exception> getExceptions() {
        return exceptions;
    }

    public LiveData<User> getCurrentUser() {
        return currentUserRepository.getCurrentUser();
    }


    public void signOut(OnSuccessListener<Void> onSuccessListener) {
        currentUserRepository.signOut(onSuccessListener);
    }


    public void saveUser(User u, OnDataCallback<User> callback) {
        userRepository.saveUser(u, callback);
    }

    public void addComment(String comment,
                           float rating,
                           User teacher,
                           User student,
                           OnDataCallback<Void> callback) {
        userRepository.addComment(comment, rating, teacher, student, callback);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        currentUserRepository.stopListening();
        teachers.stopListening();
        users.stopListening();
        myMeetings.stopListening();
    }

    public void getCurrentUserOnce(OnDataCallback<User> onDataCallback) {
        userRepository.getCurrentUser(onDataCallback);
    }

////
public void updateTeacherLocally(Teacher teacher) {
    userRepository.updateTeacherLocally(teacher);
}
}
