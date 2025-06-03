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

    public void sortChatsByLastMessage() {
        myChats.sort((c1, c2) -> {
            long t1 = c1.getMessages().isEmpty() ? c1.getCreatedAt() :
                    c1.getMessages().get(c1.getMessages().size() - 1).getTimestamp();

            long t2 = c2.getMessages().isEmpty() ? c2.getCreatedAt() :
                    c2.getMessages().get(c2.getMessages().size() - 1).getTimestamp();

            return Long.compare(t2, t1);
        });
    }

}
