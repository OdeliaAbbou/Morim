package com.example.morim.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


@Entity(tableName = "chats", primaryKeys = {"studentId", "teacherId"})
public class Chat extends BaseDocument {

    @NonNull
    private String studentId = "";
    @NonNull
    private String teacherId = "";

    private List<Message> messages = new ArrayList<>();
    private long createdAt;

    public Chat() {
    }

    public Chat(String studentId, String teacherId, List<Message> messages, long createdAt) {
        super(studentId + "_" + teacherId);
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.messages = messages;
        this.createdAt = createdAt;
    }


    public boolean isCurrentUsers() {
        String uid = FirebaseAuth.getInstance().getUid();
        if(uid==null) return false;
        return studentId.equals(uid) || teacherId.equals(uid);
    }
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Chat) {
            Chat other = (Chat) obj;
            return other.getId().equals(getId())
                    || (
                    other.getStudentId().equals(getStudentId())
                            && other.getTeacherId().equals(getTeacherId())
            );
        }
        return false;
    }
    @Override
    public String toString() {
        return "Chat{" +
                "studentId='" + studentId + '\'' +
                ", teacherId='" + teacherId + '\'' +
                ", messages=" + messages +
                ", createdAt=" + createdAt +
                '}';
    }
}
