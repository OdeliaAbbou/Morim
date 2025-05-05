package com.example.morim.model;

import java.util.ArrayList;
import java.util.List;

public class SingleChatData {
    private Teacher teacher;
    private User currentUser;
    private Chat myChat;
    public boolean allResourcesAvailable() {
        return teacher != null && myChat != null && currentUser != null;
    }

    public void setUser(Teacher user) {
        this.teacher = user;
    }

    public void setChat(Chat chat) {
        this.myChat = chat;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Chat getMyChat() {
        return myChat;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    @Override
    public String toString() {
        return "SingleChatData{" +
                "user=" + teacher +
                ", myChat=" + myChat +
                '}';
    }
}
