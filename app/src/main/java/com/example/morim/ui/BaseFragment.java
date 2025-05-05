package com.example.morim.ui;

import static com.example.morim.database.remote.FirebaseUserManager.CURRENT_USER_TYPE_KEY;

import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import javax.inject.Inject;

public class BaseFragment extends Fragment {

    protected <T extends ViewModel> T activityScopedViewModel(Class<T> viewModelClass) {
        return new ViewModelProvider(requireActivity()).get(viewModelClass);
    }

    protected <T extends ViewModel> T fragmentScopedViewModel(Class<T> viewModelClass) {
        return new ViewModelProvider(this).get(viewModelClass);
    }

    protected NavController findNavController() {
        return NavHostFragment.findNavController(this);
    }

}
