package com.example.morim;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.morim.components.TeacherCalendar;
import com.example.morim.database.OnDataCallback;
import com.example.morim.database.local.AppDatabase;
import com.example.morim.database.remote.FirebaseUserManager;
import com.example.morim.databinding.ActivityMainBinding;
import com.example.morim.model.Location;
import com.example.morim.model.Meeting;
import com.example.morim.model.Student;
import com.example.morim.model.Teacher;
import com.example.morim.model.User;
import com.example.morim.ui.dialog.TeacherDetailsDialog;
import com.example.morim.ui.main.FavoritesFragment;
import com.example.morim.util.DateUtils;
import com.example.morim.util.NotificationHelper;
import com.example.morim.viewmodel.MainViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends BaseActivity {
    private ActivityMainBinding viewBinding;

    private MainViewModel mainViewModel;

    @Inject
    protected SharedPreferences sp;

    @Inject
    protected FirebaseUserManager userManager;
    private NavController navController;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        mainViewModel = viewModel(MainViewModel.class);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);
         navController = navHostFragment.getNavController();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 106);
            }

        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        mainViewModel.getMyMeetingsData().observe(this, myMeetingsData -> {
            List<User> users = myMeetingsData.getUsers();
            List<Meeting> cancelledMeetings = myMeetingsData.getMyMeetings()
                    .stream().filter(Meeting::isCanceled)
                    .collect(Collectors.toList());

            Set<String> cancelledMeetingsInStore = sp.getStringSet("cancelledMeetings", new HashSet<>());
            Set<Meeting> freshlyCancelledMeetings = new HashSet<>();
            for (Meeting cancelled : cancelledMeetings) {
                if (!cancelledMeetingsInStore.contains(cancelled.getId())) {
                    freshlyCancelledMeetings.add(cancelled);
                }
            }
            Handler h = new Handler();
            for (Meeting freshlyCancelled : freshlyCancelledMeetings) {
                Optional<User> u = users.stream().filter(x -> x.getId().equals(
                                freshlyCancelled.getTeacherId().equals(FirebaseAuth.getInstance().getUid()) ?
                                        freshlyCancelled.getStudentId() : freshlyCancelled.getTeacherId()
                        ))
                        .findFirst();
                u.ifPresent(otherUser -> h.postDelayed(() -> NotificationHelper.sendNotification(
                        MainActivity.this,
                        String.format("Meeting scheduled at %s was cancelled by %s",
                                DateUtils.formatEpochMillis(freshlyCancelled.getMeetingDate()),
                                otherUser.getId().equals(FirebaseAuth.getInstance().getUid())
                                        ? "You" : otherUser.getFullName())
                ), 2000));
            }
            HashSet<String> newStoredCancelled = new HashSet<>(cancelledMeetingsInStore);
            newStoredCancelled.addAll(freshlyCancelledMeetings.stream().map(Meeting::getId).collect(Collectors.toList()));
            sp.edit()
                    .putStringSet("cancelledMeetings", newStoredCancelled)
                    .apply();
        });
        // Ajout de l'action sur le bouton profil


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side_navigation, menu);
        mainViewModel.getCurrentUser().observe(this, user -> {
            if (user == null) return;
            Log.d("MainActivity", "onCreateOptionsMenu: " + user);
            menu.findItem(R.id.nav_teacher_update_details)
                    .setVisible(user.isTeacher());
            mainViewModel.getCurrentUser().removeObservers(this);
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_sign_out) {
            new AlertDialog.Builder(this)
                    .setTitle("Sign out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> mainViewModel.signOut(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            startActivity(new Intent(MainActivity.this, AuthActivity.class));
                            finish();
                        }
                    }))
                    .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();

        } else if (item.getItemId() == R.id.nav_teacher_update_details) {
            User user = mainViewModel.getCurrentUser().getValue();
            if (user == null || !user.isTeacher()) return true;
            userManager.getTeacher(user.getId(), new OnDataCallback<Teacher>() {
                @Override
                public void onData(Teacher teacher) {
                    TeacherDetailsDialog c = new TeacherDetailsDialog(teacher, new TeacherDetailsDialog.OnDetailsSelected() {
                        @Override
                        public void onDetailsSelected(List<String> teachingSubjects, String teachingArea, Location teachingLocation, String education, double price) {
                            teacher.setTeachingSubjects(teachingSubjects);
                            teacher.setTeachingArea(teachingArea);
                            teacher.setTeachingLocation(teachingLocation);
                            teacher.setEducation(education);
                            teacher.setPrice(price);
                            mainViewModel.saveUser(teacher, new OnDataCallback<User>() {
                                @Override
                                public void onData(User value) {
                                    Toast.makeText(MainActivity.this, "Details updated successfully", Toast.LENGTH_SHORT).show();
                                    ////////for update details
                                    mainViewModel.updateTeacherLocally((Teacher) value);

                                }

                                @Override
                                public void onException(Exception e) {
                                    Toast.makeText(MainActivity.this, "Failed to update details", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    c.show(getSupportFragmentManager(), "Teacher details dialog");

                }

                @Override
                public void onException(Exception e) {
                    Toast.makeText(MainActivity.this, "Failed to get teacher details", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (item.getItemId() == R.id.nav_favorites) {
           new FavoritesFragment(mainViewModel).show(getSupportFragmentManager(), "Favorites");
        }

        //////////////////profile
        else if (item.getItemId() == R.id.nav_profile) {
            User user = mainViewModel.getCurrentUser().getValue();
            if (user == null || !user.isTeacher()) {
                Toast.makeText(this, "Vous n’êtes pas connecté en tant qu’enseignant.", Toast.LENGTH_SHORT).show();
                return true;
            }

            // On récupère l’ID du professeur (qui est le même que user.getId())
            String teacherId = user.getId();

            // Appel asynchrone pour obtenir l’objet Teacher complet depuis Firestore
            userManager.getTeacher(teacherId, new OnDataCallback<Teacher>() {
                @Override
                public void onData(Teacher t) {
                    if (t == null) {
                        Toast.makeText(MainActivity.this, "Profil introuvable.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 1. Sérialiser en JSON et passer au fragment
                    String teacherJson = new Gson().toJson(t);
                    Bundle bundle = new Bundle();
                    bundle.putString("teacher", teacherJson);

                    // 2. Naviguer vers TeacherFragment en lui passant le Bundle
                    navController.navigate(R.id.teacherFragment, bundle);

                }

                @Override
                public void onException(Exception e) {
                    Toast.makeText(MainActivity.this, "Erreur de chargement.", Toast.LENGTH_SHORT).show();
                }
            });

            return true;
        }


        return super.onOptionsItemSelected(item);





    }
}