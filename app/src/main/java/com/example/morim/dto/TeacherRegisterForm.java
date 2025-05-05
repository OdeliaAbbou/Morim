package com.example.morim.dto;


import com.example.morim.model.Location;

import java.util.List;

public class TeacherRegisterForm extends UserRegisterForm {
    private List<String> teachingSubjects;
    private String teachingArea;
    private String education;
    private Location teachingLocation;
    private double price;

    public TeacherRegisterForm(UserRegisterForm baseForm,
                               List<String> teachingSubjects,
                               String teachingArea,
                               Location teachingLocation,
                               String education,
                               double price) {
        super(baseForm);
        this.teachingSubjects = teachingSubjects;
        this.teachingArea = teachingArea;
        this.teachingLocation = teachingLocation;
        this.education = education;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<String> getTeachingSubjects() {
        return teachingSubjects;
    }

    public void setTeachingSubjects(List<String> teachingSubjects) {
        this.teachingSubjects = teachingSubjects;
    }

    public Location getTeachingLocation() {
        return teachingLocation;
    }

    public void setTeachingLocation(Location teachingLocation) {
        this.teachingLocation = teachingLocation;
    }

    public String getTeachingArea() {
        return teachingArea;
    }

    public void setTeachingArea(String teachingArea) {
        this.teachingArea = teachingArea;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }
}
