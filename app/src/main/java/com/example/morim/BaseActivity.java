package com.example.morim;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public abstract class BaseActivity extends AppCompatActivity {
    protected NavController findNavController() {
        if (this instanceof MainActivity)
            return Navigation.findNavController(this, R.id.main_nav_host_fragment);
        return Navigation.findNavController(this, R.id.auth_nav_host_fragment);
    }

    protected <T extends ViewModel> T viewModel(Class<T> viewModelClass) {
        return new ViewModelProvider(this).get(viewModelClass);
    }

}
