package com.example.morim.ui;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

import com.example.morim.MorimApp;

import java.util.List;

public class SubjectSpinner extends AppCompatSpinner {
    public abstract static class OnItemSelected implements AdapterView.OnItemSelectedListener {
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }
    private SubjectSpinner(Context context) {
        super(context);
    }
    public SubjectSpinner(Context context, OnItemSelected listener) {
        this(context);
        List<String> subjects = MorimApp.ALL_SUBJECTS;
        SpinnerAdapter adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, subjects);
        setOnItemSelectedListener(listener);
        setAdapter(adapter);
    }
}
