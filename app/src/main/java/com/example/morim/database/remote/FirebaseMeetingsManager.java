package com.example.morim.database.remote;

import static com.example.morim.database.remote.FirebaseUserManager.CURRENT_USER_TYPE_KEY;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.morim.database.OnDataCallback;
import com.example.morim.model.BaseDocument;
import com.example.morim.model.Chat;
import com.example.morim.model.Meeting;
import com.example.morim.util.FirebaseListener;
import com.example.morim.util.FirebaseUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class FirebaseMeetingsManager {
    private final FirebaseUtil firebaseUtil;
    private final SharedPreferences sp;


    public static final String MEETINGS_COLLECTION = "meetings";

    public FirebaseMeetingsManager(
            SharedPreferences sp,
            FirebaseUtil firebaseUtil) {
        this.sp = sp;
        this.firebaseUtil = firebaseUtil;
    }

    public Task<Void> scheduleMeeting(Meeting meeting) {
        return firebaseUtil.insertDoc(
                MEETINGS_COLLECTION,
                meeting);
    }


    public Task<Void> cancelMeeting(Meeting meeting) {
        meeting.setCanceled(true);
        meeting.setUpdatedAt(System.currentTimeMillis());
        return firebaseUtil.updateDoc(
                MEETINGS_COLLECTION,
                meeting.getId(),
                meeting);
    }

    public void getTeacherMeetings(String tid, OnDataCallback<List<Meeting>> callback) {
        firebaseUtil.getDocs(MEETINGS_COLLECTION,
                callback,
                FirebaseFirestore.getInstance()
                        .collection(MEETINGS_COLLECTION)
                        .whereEqualTo("teacherId", tid),
                Meeting.class);
    }

    public ListenerRegistration listenMyMeetings(OnDataCallback<List<Meeting>> callback) {
        String uid = FirebaseAuth.getInstance().getUid();

        return firebaseUtil.listenDocs(
                MEETINGS_COLLECTION,
                MEETINGS_COLLECTION,
                Meeting.class,
                FirebaseFirestore.getInstance()
                        .collection(MEETINGS_COLLECTION),
                new OnDataCallback<List<Meeting>>() {
                    @Override
                    public void onData(List<Meeting> value) {
                        List<Meeting> meetings;
                        meetings = value.stream()
                                .filter(meeting -> (Objects.equals(meeting.getTeacherId(), uid)) || Objects.equals(meeting.getStudentId(), uid))
                                .collect(Collectors.toList());
                        callback.onData(meetings);
                    }

                    @Override
                    public void onException(Exception e) {
                        callback.onException(e);
                    }
                });
    }

}
