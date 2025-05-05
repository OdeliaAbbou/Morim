package com.example.morim.database.local;

import androidx.room.TypeConverter;

import com.example.morim.model.Comment;
import com.example.morim.model.Location;
import com.example.morim.model.Meeting;
import com.example.morim.model.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public class Converters {
    private final Gson g = new Gson();

    @TypeConverter
    public List<String> fromStringToListOfStrings(String str) {
        return g.fromJson(str, new TypeToken<List<String>>() {
        }.getType());
    }


    @TypeConverter
    public List<Comment> fromCommentStringToListOfComments(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        Type listType = new TypeToken<List<Comment>>() {
        }.getType();
        return g.fromJson(str, listType);
    }

    @TypeConverter
    public String fromListOfCommentsToString(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return g.toJson(comments);
    }

    @TypeConverter
    public List<Message> fromStringToListOfMessages(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        Type listType = new TypeToken<List<Message>>() {
        }.getType();
        return g.fromJson(str, listType);
    }

    @TypeConverter
    public String fromListOfMessagesToString(List<Message> messages) {
        if (messages == null) {
            return null;
        }
        return g.toJson(messages);
    }

    @TypeConverter
    public String fromListOfStringsToString(List<String> str) {
        return g.toJson(str);
    }


    @TypeConverter
    public Location fromStringToLocation(String str) {
        return g.fromJson(str, Location.class);
    }

    @TypeConverter
    public String fromLocationToString(Location location) {
        return g.toJson(location);
    }


}
