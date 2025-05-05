package com.example.morim.database.common;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.morim.database.OnDataCallback;
import com.example.morim.database.local.MeetingDao;
import com.example.morim.database.remote.FirebaseMeetingsManager;
import com.example.morim.model.Meeting;
import com.example.morim.util.FirebaseListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

public class MeetingRepository {

    private final ScheduledThreadPoolExecutor executor;
    private final FirebaseMeetingsManager remoteDb;
    private final MeetingDao meetingDao;

    public MeetingRepository(
            ScheduledThreadPoolExecutor executor,
            MeetingDao meetingDao,
            FirebaseMeetingsManager remoteDb
    ) {
        this.executor = executor;
        this.meetingDao = meetingDao;
        this.remoteDb = remoteDb;
    }

    public void getTeacherMeetings(String tid, OnDataCallback<List<Meeting>> callback) {
        remoteDb.getTeacherMeetings(tid, callback);
    }

    public Task<Void> scheduleMeeting(Meeting meeting) {
        return remoteDb.scheduleMeeting(meeting);
    }

    public Task<Void> cancelMeeting(Meeting meeting) {
        return remoteDb.cancelMeeting(meeting);
    }


    public FirebaseListener<List<Meeting>> listenMyMeetings() {
        return new FirebaseListener<>(
                meetingDao.listenMyMeetings(),
                remoteDb.listenMyMeetings(new OnDataCallback<List<Meeting>>() {
                    @Override
                    public void onData(List<Meeting> value) {
                        executor.execute(() -> {
                            meetingDao.removeAll();
                            meetingDao.insert(value);
                        });
                    }

                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
                    }
                }));
    }

}
