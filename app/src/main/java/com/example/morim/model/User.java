package com.example.morim.model;


import androidx.room.Entity;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "current_user")
public class User extends BaseDocument{

    public static final String DEFAULT_IMAGE = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/65/No-Image-Placeholder.svg/660px-No-Image-Placeholder.svg.png?20200912122019";
    private String email;
    private String fullName;
    private List<Comment> comments;
    private String address;

    private String phone;
    private String image;

    private boolean isTeacher;

    public User(String id, String email, String fullName, String phone, String address, String image, boolean isTeacher) {
        super(id);
        this.email = email;
        this.fullName = fullName;
        this.address = address;
        this.isTeacher = isTeacher;
        this.phone = phone;
        this.image = image;
    }

public String getImage() {
    if(image == null || image.isEmpty())
        return DEFAULT_IMAGE;
    return image;
}

    public void setImage(String image) {
        this.image = image;
    }

    public User() {
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isTeacher() {
        return isTeacher;
    }

    public void setTeacher(boolean teacher) {
        isTeacher = teacher;
    }

    public List<Comment> getComments() {
        if (comments == null)
            comments = new ArrayList<>();
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }


    public void addComment(Comment comment) {
        if (comments == null)
            comments = new ArrayList<>();
        comments.add(comment);
    }

}
