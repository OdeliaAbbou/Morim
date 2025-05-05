package com.example.morim.model;

import androidx.room.Entity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;


@Entity(tableName = "teachers")
public class Teacher extends User {

    public Teacher(Map<String, Object> values) {
        Gson g = new Gson();
        g.fromJson(String.valueOf(values), Teacher.class);
    }

    private List<String> teachingSubjects;
    private String teachingArea;
    private String education;

    private List<String> ratingStudents;

    private double averageRating;


    private double price;
    private Location teachingLocation;


    public Teacher(String id, String email, String fullName, String address, String phone, String image, List<String> teachingSubjects, String teachingArea, String education, List<String> ratingStudents, Location location, double averageRating, double price) {
        super(id, email, fullName, address, phone, image, true);
        this.teachingSubjects = teachingSubjects;
        this.teachingArea = teachingArea;
        this.education = education;
        this.ratingStudents = ratingStudents;
        this.averageRating = averageRating;
        this.teachingLocation = location;
        this.price = price;
    }

    public Teacher() {
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

    public List<String> getRatingStudents() {
        return ratingStudents;
    }

    public void setRatingStudents(List<String> ratingStudents) {
        this.ratingStudents = ratingStudents;
    }

    public double getAverageRating() {
        Stream<Comment> commentStream = getComments().stream();
        averageRating = commentStream.mapToDouble(Comment::getRating).average().orElse(0);
        return averageRating;
    }
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

}
