package com.example.morim.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;

import com.example.morim.ChatActivity;
import com.example.morim.adapter.TeacherAdapter;
import com.example.morim.database.OnDataCallback;
import com.example.morim.databinding.FragmentFavoritesBinding;
import com.example.morim.model.Favorite;
import com.example.morim.model.MyMeetingsData;
import com.example.morim.model.Student;
import com.example.morim.model.Teacher;
import com.example.morim.model.User;
import com.example.morim.ui.dialog.RatingDialog;
import com.example.morim.ui.dialog.RatingListDialog;
import com.example.morim.viewmodel.MainViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesFragment extends DialogFragment implements TeacherAdapter.TeacherAdapterListener {

    private FragmentFavoritesBinding binding;

    private MainViewModel mainViewModel;
    private Student stud;

    public FavoritesFragment(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private TeacherAdapter adapter;
    private List<Teacher> teachers = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.closeBtn.setOnClickListener(v -> dismiss());
        mainViewModel.getMyFavorites().observe(getViewLifecycleOwner(), favorites -> {
            if (adapter != null) {
                adapter.filterFavorites(teachers, new HashSet<>(favorites));
                adapter.setFavorites(new HashSet<>(favorites));
            }
            if(favorites.isEmpty()) {
                binding.noFavorites.setVisibility(View.VISIBLE);
            } else {
                binding.noFavorites.setVisibility(View.GONE);
            }
        });

        mainViewModel.getTeachers()
                .observe(getViewLifecycleOwner(), teachers -> {
                    FavoritesFragment.this.teachers = teachers;
                    adapter = new TeacherAdapter(teachers, FavoritesFragment.this);
                    List<Favorite> favorites = mainViewModel.getMyFavorites().getValue();
                    if (favorites != null) {
                        adapter.setFavorites(new HashSet<>(favorites));
                        adapter.filterFavorites(teachers, new HashSet<>(favorites));
                    }
                    binding.rvTeachers.setAdapter(adapter);
                });

        mainViewModel.getMyFavorites()
                .observe(getViewLifecycleOwner(), favorites -> {
                    if (adapter != null && favorites != null) {
                        adapter.setFavorites(new HashSet<>(favorites));
                    }
                });

        mainViewModel.getCurrentUserOnce(new OnDataCallback<User>() {
            @Override
            public void onData(User user) {
                if (adapter != null && user instanceof Student) {
                    adapter.updateCurrentUser((Student) user);
                    stud = (Student) user;
                } else {
                    Log.d("HomeFragment", "onChanged: User is not student");
                }
            }

            @Override
            public void onException(Exception e) {

            }
        });

        mainViewModel.getMyMeetingsData().observe(getViewLifecycleOwner(), new Observer<MyMeetingsData>() {
            @Override
            public void onChanged(MyMeetingsData myMeetingsData) {
                if (adapter != null && myMeetingsData.allResourcesAvailable()) {
                    adapter.updateCurrentMeetings(myMeetingsData.getMyMeetings());
                }
            }
        });
    }


    @Override
    public void onSendMessage(Teacher teacher) {
        String teacherId = teacher.getId(); // Obtenir l'ID de l'enseignant depuis l'objet Teacher
        if (teacherId == null || teacherId.isEmpty()) {
            Toast.makeText(requireContext(), "Erreur : L'ID de l'enseignant est introuvable", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("onSendMessage", teacher.toString());
        // Lancer ChatActivity avec les informations nécessaires
        Intent intent = new Intent(requireContext(), ChatActivity.class);
        intent.putExtra("TEACHER_ID", teacherId); // Transmettre l'ID du professeur
        intent.putExtra("TEACHER_NAME", teacher.getFullName()); // Optionnel : Nom du professeur
        requireContext().startActivity(intent);
    }

    @Override
    public void onAddReview(Teacher teacher) {

        if (stud == null) {
            Toast.makeText(requireContext(), "Error: User not found", Toast.LENGTH_SHORT).show();
            return;
        }
        if (stud.isTeacher()) {
            Toast.makeText(requireContext(), "Teachers cannot rate other teachers", Toast.LENGTH_SHORT).show();
            return;
        }

        RatingDialog.showDialog((rating, comment) ->
                mainViewModel.addComment(comment, rating, teacher, stud, new OnDataCallback<Void>() {
                    @Override
                    public void onData(Void value) {
                        Toast.makeText(requireContext(), "Review added successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onException(Exception e) {
                        Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }), getChildFragmentManager());
    }

    @Override
    public void onShowComments(Teacher teacher) {
        RatingListDialog.showRatingListDialog(teacher.getComments(), getChildFragmentManager());
    }

    @Override
    public void onAddToFavorites(Teacher teacher) {
        mainViewModel.addFavorite(teacher.getId())
                .addOnSuccessListener(favorite -> {
                    Snackbar.make(binding.getRoot(), "Teacher added to favorites", Snackbar.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(binding.getRoot(), "Failed to add teacher to favorites", Snackbar.LENGTH_LONG).show();
                });
    }

    @Override
    public void onRemoveFromFavorites(Teacher teacher) {
        mainViewModel.removeFavorite(teacher.getId())
                .addOnSuccessListener(favorite -> {
                    Snackbar.make(binding.getRoot(), "Teacher removed from favorites", Snackbar.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(binding.getRoot(), "Failed to remove teacher from favorites", Snackbar.LENGTH_LONG).show();
                });
    }

    @Override
    public void onRequestScheduleWithTeacher(Teacher teacher) {
        String cid = FirebaseAuth.getInstance().getUid();
        if (cid != null && cid.equals(teacher.getId())) {
            Snackbar.make(binding.getRoot(), "Cannot schedule with your self", Snackbar.LENGTH_LONG).show();
            return;
        }

        mainViewModel.showScheduleMeetingDialog(
                requireContext(),
                getChildFragmentManager(),
                teacher
        );

    }

    @Override
    public void onViewTeacher(Teacher t) {
        Log.d("onViewTeacher", new Gson().toJson(t));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (mainViewModel != null) {
            mainViewModel.getMyFavorites().removeObservers(getViewLifecycleOwner());
            mainViewModel.getTeachers().removeObservers(getViewLifecycleOwner());
            mainViewModel.getMyMeetingsData().removeObservers(getViewLifecycleOwner());
            mainViewModel.getCurrentUser().removeObservers(getViewLifecycleOwner());
            mainViewModel = null;


        }
    }
}
