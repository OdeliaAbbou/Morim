package com.example.morim.model;

import java.util.ArrayList;
import java.util.List;

public class MyMeetingsData {
    private List<User> users = new ArrayList<>();
    private List<Meeting> myMeetings = new ArrayList<>();
    public boolean allResourcesAvailable() {
        return users != null && myMeetings != null;
    }
    public void setUsers(List<User> users) {
        this.users = users;
    }
    public void setMyMeetings(List<Meeting> myMeetings) {
        this.myMeetings = myMeetings;
    }

    public List<Meeting> getMyMeetings() {
        return myMeetings;
    }

    public List<User> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "MyMeetingsData{" +
                "users=" + users +
                ", myMeetings=" + myMeetings +
                '}';
    }
}
