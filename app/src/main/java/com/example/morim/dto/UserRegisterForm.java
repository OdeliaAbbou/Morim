package com.example.morim.dto;

import android.net.Uri;

import androidx.annotation.Nullable;

public class UserRegisterForm {
    private String email;
    private String password;

    private String fullName;

    private String address;

    private String phone;

    @Nullable
    private Uri image;

    public UserRegisterForm(String email, String password, String fullName, String address, String phone, @Nullable Uri image) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.address = address;
        this.image = image;
        this.phone = phone;
    }

    public UserRegisterForm(UserRegisterForm other) {
        this.email = other.email;
        this.password = other.password;
        this.fullName = other.fullName;
        this.address = other.address;
        this.image = other.image;
        this.phone = other.phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Nullable
    public Uri getImage() {
        return image;
    }

    public void setImage(@Nullable Uri image) {
        this.image = image;
    }
}
