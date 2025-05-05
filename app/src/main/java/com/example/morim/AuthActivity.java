package com.example.morim;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.example.morim.database.local.AppDatabase;
import com.example.morim.databinding.ActivityAuthBinding;
import com.example.morim.model.User;
import com.example.morim.util.LoadingState;
import com.example.morim.viewmodel.AuthViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthActivity extends BaseActivity {
    private ActivityAuthBinding viewBinding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        authViewModel = viewModel(AuthViewModel.class);


        if (FirebaseAuth.getInstance().getCurrentUser() != null && !getIntent().getBooleanExtra("LOGOUT", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        authViewModel.getExceptions().observe(this, e -> {
            if (e.getMessage() != null)
                Snackbar.make(viewBinding.getRoot(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        });

        authViewModel.getLoadingState().observe(this, loadingState -> {
            viewBinding.pbAuth.setVisibility(loadingState == LoadingState.Loading ? View.VISIBLE : View.GONE);
        });
    }


}
