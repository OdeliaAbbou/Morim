package com.example.morim.database;

public interface OnDataCallback<T>{
    void onData(T value);
    void onException(Exception e);
}
