package com.example.morim.model;

import androidx.annotation.Nullable;
import androidx.room.Entity;

import java.util.Objects;

@Entity(tableName = "favorites")
public class Favorite extends BaseDocument {
    private String teacherId;

    public Favorite(String teacherId) {
        this.teacherId = teacherId;
    }

    public Favorite() {
    }

    @Override
    public String getId() {
        return teacherId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Favorite)) return false;
        Favorite other = (Favorite) o;
        return teacherId != null && teacherId.equals(other.teacherId);
    }

    @Override
    public int hashCode() {
        return teacherId != null ? teacherId.hashCode() : 0;
    }
}
