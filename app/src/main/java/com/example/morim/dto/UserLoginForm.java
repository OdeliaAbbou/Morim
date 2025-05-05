package com.example.morim.dto;

public class UserLoginForm {

    private boolean teacher;

    private String email;
    private String password;

    public UserLoginForm(String email, String password, boolean teacher) {
        this.email = email;
        this.password = password;
        this.teacher = teacher;
    }

    public boolean isTeacher() {
        return teacher;
    }

    public void setTeacher(boolean teacher) {
        this.teacher = teacher;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
