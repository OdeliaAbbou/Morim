package com.example.morim.ui.auth;


import static com.example.morim.database.remote.FirebaseUserManager.CURRENT_USER_TYPE_KEY;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.morim.MainActivity;
import com.example.morim.R;
import com.example.morim.database.OnDataCallback;
import com.example.morim.databinding.FragmentRegisterBinding;
import com.example.morim.dto.StudentRegisterForm;
import com.example.morim.dto.TeacherRegisterForm;
import com.example.morim.dto.UserRegisterForm;
import com.example.morim.model.User;
import com.example.morim.ui.BaseFragment;
import com.example.morim.ui.dialog.TeacherDetailsDialog;
import com.example.morim.viewmodel.AuthViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterFragment extends BaseFragment {


    private TeacherDetailsDialog teacherDetailsDialog;

    private AuthViewModel authViewModel;
    private FragmentRegisterBinding viewBinding;

    private ActivityResultLauncher<String> mGetImage;

    private Uri selectedImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentRegisterBinding.inflate(inflater, container, false);
        authViewModel = activityScopedViewModel(AuthViewModel.class);
        return viewBinding.getRoot();
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        viewBinding.btnBackToSignIn.setOnClickListener(view12 -> findNavController()
//                .popBackStack());
//        mGetImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
//                uri -> {
//                    // Handle the returned Uri, uri is the result from the gallery
//                    if (uri != null) {
//                        // Use the uri to access the image
//                        this.selectedImage = uri;
//                        viewBinding.ivRegisterUserImage.setImageURI(selectedImage);
//                    }
//                });
//        viewBinding.ivRegisterUserImage.setOnClickListener(this::openGallery);
//        requestLocationPermissions();
//
//        viewBinding.btnRegisterSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String fullName = viewBinding.etFullNameRegister.getText().toString();
//                String address = viewBinding.etAddressRegister.getText().toString();
//                String email = viewBinding.etEmailRegister.getText().toString();
//                String password = viewBinding.etPasswordRegister.getText().toString();
//                String phone = viewBinding.etPhoneRegister.getText().toString();
//
//                if (email.isEmpty()) {
//                    Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_LONG).show();
//                    viewBinding.etLayoutEmailAddressRegister.setError("Email must not be empty!");
//                    return;
//                }
//                if (password.isEmpty()) {
//                    Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_LONG).show();
//                    viewBinding.etLayoutPasswordRegister.setError("Password must not be empty!");
//                    return;
//                }
//
//                if (fullName.isEmpty()) {
//                    Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_LONG).show();
//                    viewBinding.etLayoutPasswordRegister.setError("Full name must not be empty!");
//                    return;
//                }
//                if (address.isEmpty()) {
//                    Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_LONG).show();
//                    viewBinding.etLayoutPasswordRegister.setError("Address must not be empty!");
//                    return;
//                }
//                if (phone.isEmpty()) {
//                    Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_LONG).show();
//                    viewBinding.etLayoutPhoneRegister.setError("Phone must not be empty!");
//                    return;
//                }
//                //ajoute maintenant
//                if (!phone.matches("\\d{10}")) {
//                    Toast.makeText(getContext(),  "Phone number must be exactly 10 digits", Toast.LENGTH_LONG).show();
//                    viewBinding.etLayoutPhoneRegister.setError("Invalid phone number!");
//                    return;
//                }
//                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                    Toast.makeText(getContext(),  "Please enter a valid email address", Toast.LENGTH_LONG).show();
//                    viewBinding.etLayoutPhoneRegister.setError("Invalid email format!");
//                    return;
//                }
//
//
//                boolean isTeacher = viewBinding.typeRg.getCheckedRadioButtonId() == viewBinding.typeTeacher.getId();
//
//                if (isTeacher) {
//                    if (teacherDetailsDialog == null) {
//                        teacherDetailsDialog = new TeacherDetailsDialog((teachingSubjects, teachingAreas, teachingLocation, education, price) -> {
//                            UserRegisterForm form = new TeacherRegisterForm(new UserRegisterForm(
//                                    email, password, fullName, address, phone, selectedImage
//                            ), teachingSubjects, teachingAreas, teachingLocation, education, price);
//                            authViewModel.createUser(form, new OnDataCallback<User>() {
//                                @Override
//                                public void onData(User value) {
//                                    SharedPreferences.Editor editor = requireActivity().getSharedPreferences(CURRENT_USER_TYPE_KEY, 0).edit();
//                                    editor.putBoolean(CURRENT_USER_TYPE_KEY, true);
//                                    editor.apply();
//                                    Toast.makeText(requireContext(), "User created successfully", Toast.LENGTH_LONG).show();
//                                    requireActivity().finish();
//                                    startActivity(new Intent(requireActivity(), MainActivity.class));
//                                }
//
//                                @Override
//                                public void onException(Exception e) {
//
//                                    Snackbar.make(viewBinding.getRoot(), e.getMessage(), Snackbar.LENGTH_LONG).show();
//                                }
//                            });
//                        });
//                    } else {
//                        teacherDetailsDialog.setListener((teachingSubjects, teachingAreas, teachingLocation, education, price) -> {
//                            UserRegisterForm form = new TeacherRegisterForm(new UserRegisterForm(
//                                    email, password, fullName, address, phone, selectedImage
//                            ), teachingSubjects, teachingAreas, teachingLocation, education, price);
//                            authViewModel.createUser(form, new OnDataCallback<User>() {
//                                @Override
//                                public void onData(User value) {
//                                    SharedPreferences.Editor editor = requireActivity().getSharedPreferences(CURRENT_USER_TYPE_KEY, 0).edit();
//                                    editor.putBoolean(CURRENT_USER_TYPE_KEY, true);
//                                    editor.apply();
//                                    Toast.makeText(requireContext(), "User created successfully", Toast.LENGTH_LONG).show();
//                                    requireActivity().finish();
//                                    startActivity(new Intent(requireActivity(), MainActivity.class));
//                                }
//
//                                @Override
//                                public void onException(Exception e) {
//                                    Snackbar.make(viewBinding.getRoot(), e.getMessage(), Snackbar.LENGTH_LONG).show();
//                                }
//                            });
//                        });
//                    }
//                    teacherDetailsDialog.show(getChildFragmentManager(), "Teacher details dialog");
//                } else {
//                    UserRegisterForm form = new StudentRegisterForm(new UserRegisterForm(
//                            email, password, fullName, address, phone, selectedImage
//                    ));
//                    authViewModel.createUser(form, new OnDataCallback<User>() {
//                        @Override
//                        public void onData(User value) {
//                            SharedPreferences.Editor editor = requireActivity().getSharedPreferences(CURRENT_USER_TYPE_KEY, 0).edit();
//                            editor.putBoolean(CURRENT_USER_TYPE_KEY, false);
//                            editor.apply();
//                            Toast.makeText(requireContext(), "User created successfully", Toast.LENGTH_LONG).show();
//                            requireActivity().finish();
//                            startActivity(new Intent(requireActivity(), MainActivity.class));
//                        }
//
//                        @Override
//                        public void onException(Exception e) {
//                            Snackbar.make(viewBinding.getRoot(), e.getMessage(), Snackbar.LENGTH_LONG).show();
//                        }
//                    });
//                }
//            }
//        });
//    }
@Override
public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    viewBinding.btnBackToSignIn.setOnClickListener(view12 -> findNavController()
            .popBackStack());

    mGetImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                // Handle the returned Uri, uri is the result from the gallery
                if (uri != null) {
                    // Use the uri to access the image
                    this.selectedImage = uri;
                    viewBinding.ivRegisterUserImage.setImageURI(selectedImage);
                }
            });

    viewBinding.ivRegisterUserImage.setOnClickListener(this::openGallery);
    requestLocationPermissions();

    viewBinding.btnRegisterSubmit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Récupération des données du formulaire
            String fullName = viewBinding.etFullNameRegister.getText().toString().trim();
            String address = viewBinding.etAddressRegister.getText().toString().trim();
            String email = viewBinding.etEmailRegister.getText().toString().trim();
            String password = viewBinding.etPasswordRegister.getText().toString().trim();
            String phone = viewBinding.etPhoneRegister.getText().toString().trim();

            // Reset des erreurs précédentes
            viewBinding.etLayoutEmailAddressRegister.setError(null);
            viewBinding.etLayoutPasswordRegister.setError(null);
            viewBinding.etLayoutFullNameRegister.setError(null);
            viewBinding.etLayoutAddressRegister.setError(null);
            viewBinding.etLayoutPhoneRegister.setError(null);

            // Validation email
            if (email.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_LONG).show();
                viewBinding.etLayoutEmailAddressRegister.setError("Email must not be empty!");
                return;
            }

            // Validation format email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getContext(), "Please enter a valid email address", Toast.LENGTH_LONG).show();
                viewBinding.etLayoutEmailAddressRegister.setError("Invalid email format!");
                return;
            }

            // Validation password
            if (password.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_LONG).show();
                viewBinding.etLayoutPasswordRegister.setError("Password must not be empty!");
                return;
            }

            // Validation nom complet
            if (fullName.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_LONG).show();
                viewBinding.etLayoutFullNameRegister.setError("Full name must not be empty!");
                return;
            }

            // Validation adresse
            if (address.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_LONG).show();
                viewBinding.etLayoutAddressRegister.setError("Address must not be empty!");
                return;
            }

            // Validation téléphone
            if (phone.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_LONG).show();
                viewBinding.etLayoutPhoneRegister.setError("Phone must not be empty!");
                return;
            }

            // Validation format téléphone (10 chiffres)
            if (!phone.matches("\\d{10}")) {
                Toast.makeText(getContext(), "Phone number must be exactly 10 digits", Toast.LENGTH_LONG).show();
                viewBinding.etLayoutPhoneRegister.setError("Invalid phone number!");
                return;
            }

            // Vérifier le type d'utilisateur sélectionné
            boolean isTeacher = viewBinding.typeRg.getCheckedRadioButtonId() == viewBinding.typeTeacher.getId();

            if (isTeacher) {
                // Création du dialog pour les détails du professeur
                if (teacherDetailsDialog == null) {
                    teacherDetailsDialog = new TeacherDetailsDialog((teachingSubjects, teachingAreas, teachingLocation, education, price) -> {
                        Log.d("REGISTER_DEBUG", "selectedImage avant création form: " + (selectedImage != null ? selectedImage.toString() : "NULL"));

                        UserRegisterForm form = new TeacherRegisterForm(new UserRegisterForm(
                                email, password, fullName, address, phone, selectedImage
                        ), teachingSubjects, teachingAreas, teachingLocation, education, price);

                        Log.d("REGISTER_DEBUG", "Image dans le form: " + (form.getImage() != null ? form.getImage().toString() : "NULL"));


                        authViewModel.createUser(form, new OnDataCallback<User>() {
                            @Override
                            public void onData(User value) {
                                SharedPreferences.Editor editor = requireActivity().getSharedPreferences(CURRENT_USER_TYPE_KEY, 0).edit();
                                editor.putBoolean(CURRENT_USER_TYPE_KEY, true);
                                editor.apply();
                                Toast.makeText(requireContext(), "User created successfully", Toast.LENGTH_LONG).show();
                                requireActivity().finish();
                                startActivity(new Intent(requireActivity(), MainActivity.class));
                            }

                            @Override
                            public void onException(Exception e) {
                                Snackbar.make(viewBinding.getRoot(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                    });
                } else {
                    teacherDetailsDialog.setListener((teachingSubjects, teachingAreas, teachingLocation, education, price) -> {
                        UserRegisterForm form = new TeacherRegisterForm(new UserRegisterForm(
                                email, password, fullName, address, phone, selectedImage
                        ), teachingSubjects, teachingAreas, teachingLocation, education, price);
                        authViewModel.createUser(form, new OnDataCallback<User>() {
                            @Override
                            public void onData(User value) {
                                SharedPreferences.Editor editor = requireActivity().getSharedPreferences(CURRENT_USER_TYPE_KEY, 0).edit();
                                editor.putBoolean(CURRENT_USER_TYPE_KEY, true);
                                editor.apply();
                                Toast.makeText(requireContext(), "User created successfully", Toast.LENGTH_LONG).show();
                                requireActivity().finish();
                                startActivity(new Intent(requireActivity(), MainActivity.class));
                            }

                            @Override
                            public void onException(Exception e) {
                                Snackbar.make(viewBinding.getRoot(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                    });
                }
                teacherDetailsDialog.show(getChildFragmentManager(), "Teacher details dialog");
            } else {
                // Création d'un étudiant
                UserRegisterForm form = new StudentRegisterForm(new UserRegisterForm(
                        email, password, fullName, address, phone, selectedImage
                ));
                authViewModel.createUser(form, new OnDataCallback<User>() {
                    @Override
                    public void onData(User value) {
                        SharedPreferences.Editor editor = requireActivity().getSharedPreferences(CURRENT_USER_TYPE_KEY, 0).edit();
                        editor.putBoolean(CURRENT_USER_TYPE_KEY, false);
                        editor.apply();
                        Toast.makeText(requireContext(), "User created successfully", Toast.LENGTH_LONG).show();
                        requireActivity().finish();
                        startActivity(new Intent(requireActivity(), MainActivity.class));
                    }

                    @Override
                    public void onException(Exception e) {
                        Snackbar.make(viewBinding.getRoot(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }
    });
}



    private final ActivityResultLauncher<String[]> mPermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> o) {
            boolean isAllConfirmed = !o.containsValue(false);
            if (!isAllConfirmed)
                Toast.makeText(requireContext(), "Location permissions were not granted, if you create a teacher account, you will not appear in location results", Toast.LENGTH_LONG).show();
        }
    });

    private void requestLocationPermissions() {
        if (requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED ||
                requireActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_DENIED)
            mPermissions.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
    }


    private void openGallery(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            mGetImage.launch("image/*");
        } else {
            if (requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            }


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewBinding = null;
    }
}
