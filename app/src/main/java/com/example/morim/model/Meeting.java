package com.example.morim.model;

import androidx.room.Entity;

@Entity(tableName = "my_meetings")
public class Meeting extends BaseDocument {
    private String studentId;
    private String teacherId;
    private Long meetingDate;
    private String meetingSubject;
    private boolean teacherSeen;
    private boolean canceled;
    public Meeting(String id, String studentId, String teacherId, Long meetingDate, String meetingSubject) {
        super(id);
        this.id = id;
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.meetingDate = meetingDate;
        this.meetingSubject = meetingSubject;
    }

    public Meeting() {
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public void setTeacherSeen(boolean teacherSeen) {
        this.teacherSeen = teacherSeen;
    }

    public boolean isTeacherSeen() {
        return teacherSeen;
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

    public Long getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(Long meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getMeetingSubject() {
        return meetingSubject;
    }

    public void setMeetingSubject(String meetingSubject) {
        this.meetingSubject = meetingSubject;
    }
}
