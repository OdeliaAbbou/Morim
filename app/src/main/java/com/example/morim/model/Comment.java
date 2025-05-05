package com.example.morim.model;

import java.util.List;
import java.util.UUID;

public class Comment implements Comparable<Comment> {

    private String id = UUID.randomUUID().toString();
    private String studentId;
    private String teacherId;
    private String comment;
    private String studentName;
    private float rating;

    private long timestamp = System.currentTimeMillis();


    public Comment(String studentId, String teacherId, String comment, String studentName, float rating) {
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.comment = comment;
        this.studentName = studentName;
        this.rating = rating;
    }

    public Comment() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment1 = (Comment) o;
        return id.equals(comment1.id) || (studentId.equals(comment1.studentId) && teacherId.equals(comment1.teacherId));
    }

    @Override
    public int compareTo(Comment comment) {
        return Long.compare(comment.getTimestamp(), this.getTimestamp());
    }

    public static void sortComments(List<Comment> comments) {
        comments.sort(Comment::compareTo);
    }
}
