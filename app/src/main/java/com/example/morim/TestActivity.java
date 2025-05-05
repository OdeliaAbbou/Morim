package com.example.morim;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.morim.databinding.ActivityTestBinding;

public class TestActivity extends BaseActivity {

    private ActivityTestBinding viewBinding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

    }
}
