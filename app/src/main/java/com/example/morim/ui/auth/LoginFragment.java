package com.example.morim.ui.auth;

import static com.example.morim.database.remote.FirebaseUserManager.CURRENT_USER_TYPE_KEY;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.morim.MainActivity;
import com.example.morim.R;
import com.example.morim.database.OnDataCallback;
import com.example.morim.databinding.FragmentLoginBinding;
import com.example.morim.dto.UserLoginForm;
import com.example.morim.model.User;
import com.example.morim.ui.BaseFragment;
import com.example.morim.viewmodel.AuthViewModel;
import com.google.firebase.auth.AuthResult;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends BaseFragment {

    private FragmentLoginBinding viewBinding;

    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentLoginBinding.inflate(inflater, container, false);
        authViewModel = activityScopedViewModel(AuthViewModel.class);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        viewBinding.btnToRegister.setOnClickListener(v -> findNavController()
                .navigate(R.id.action_loginFragment_to_registerFragment));


        viewBinding.btnLoginSubmit.setOnClickListener(v -> {


            String email = viewBinding.etEmailLogin.getText().toString();
            String password = viewBinding.etPasswordLogin.getText().toString();

            if (email.isEmpty()) {
                Toast.makeText(getContext(), "Please fill both email and password", Toast.LENGTH_LONG).show();
                viewBinding.etLayoutEmailLogin.setError("Email must not be empty!");
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(getContext(), "Please fill both email and password", Toast.LENGTH_LONG).show();
                viewBinding.etLayoutPasswordLogin.setError("password must not be empty!");
                return;
            }
            UserLoginForm form = new UserLoginForm(email, password, false);


            authViewModel.signIn(form, new OnDataCallback<AuthResult>() {
                @Override
                public void onData(AuthResult value) {
                    requireActivity().finish();
                    startActivity(new Intent(requireActivity(), MainActivity.class));

                    Toast.makeText(getContext(), "Hi " + value.getUser().getEmail(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onException(Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewBinding = null;
    }
}
