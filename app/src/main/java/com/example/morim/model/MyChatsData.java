package com.example.morim.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MyChatsData {
    private List<User> users = new ArrayList<>();
    private List<Chat> myChats = new ArrayList<>();

    private HashMap<String, User> idToUser = new HashMap<>();

    public boolean allResourcesAvailable() {
        return users != null && myChats != null && !users.isEmpty() && !myChats.isEmpty();
    }

    public void setUsers(List<User> users) {
        this.users = users;
        initUserMap();
    }

    public HashMap<String, User> idToUser() {
        return idToUser;
    }

    public void setMyMeetings(List<Chat> myMeetings) {
        this.myChats = myMeetings;
    }

    private void initUserMap() {
        for (User u : users) {
            idToUser.put(u.getId(), u);
        }
    }

    public List<Chat> getMyChats() {
        return myChats;
    }

    public List<User> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "MyMeetingsData{" +
                "users=" + users +
                ", myMeetings=" + myChats +
                '}';
    }
}
