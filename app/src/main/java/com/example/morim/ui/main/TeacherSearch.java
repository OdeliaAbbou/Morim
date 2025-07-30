package com.example.morim.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.morim.ChatActivity;
import com.example.morim.MorimApp;
import com.example.morim.R;
import com.example.morim.adapter.TeacherAdapter;
import com.example.morim.database.OnDataCallback;
import com.example.morim.databinding.FragmentTeacherSearchBinding;
import com.example.morim.model.Student;
import com.example.morim.model.Teacher;
import com.example.morim.model.User;
import com.example.morim.ui.BaseFragment;
import com.example.morim.ui.SubjectSpinner;
import com.example.morim.ui.dialog.RatingDialog;
import com.example.morim.ui.dialog.RatingListDialog;
import com.example.morim.viewmodel.MainViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TeacherSearch extends BaseFragment {

    private FragmentTeacherSearchBinding binding;
    private MainViewModel mainViewModel;

    private TeacherAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTeacherSearchBinding.inflate(inflater, container, false);
        mainViewModel = activityScopedViewModel(MainViewModel.class);
        return binding.getRoot();




    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Variable pour enregistrer la catégorie sélectionnée
        final String[] selectedCategory = {null};

        // Initialisation du Spinner
        Spinner spinner = new SubjectSpinner(getContext(), new SubjectSpinner.OnItemSelected() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0){
                    //////////
                    selectedCategory[0] = null;
                    // 👉 Remettre la liste complète
                    mainViewModel.getTeachers().observe(getViewLifecycleOwner(), teachers -> {
                        adapter.setTeacherData(teachers);
                    });

                    ////////////
                    return;
                }
                selectedCategory[0] = MorimApp.ALL_SUBJECTS.get(i);
            }
        });
        spinner.setOnTouchListener((v, motionEvent) -> {
            spinner.setSelection(0);
            spinner.performClick();
            return true;
        });
        spinner.setPadding(16, 0, 16, 0);
        binding.subjectSpinnerLayout.addView(spinner);

        // Initialisation de l'Adapter
        adapter = new TeacherAdapter(new ArrayList<>(), new TeacherAdapter.TeacherAdapterListener() {
            @Override
            public void onViewTeacher(Teacher t) {
                Gson g = new Gson();
                TeacherSearchDirections.ActionTeacherSearchToTeacherFragment intent =
                        TeacherSearchDirections.actionTeacherSearchToTeacherFragment(g.toJson(t));
                findNavController().navigate(intent);
            }

            @Override
            public void onRequestScheduleWithTeacher(Teacher teacher) {
                String cid = FirebaseAuth.getInstance().getUid();
                if (cid != null && cid.equals(teacher.getId())) {
                    Snackbar.make(binding.getRoot(), "Cannot schedule with yourself", Snackbar.LENGTH_LONG).show();
                    return;
                }
                mainViewModel.showScheduleMeetingDialog(
                        requireContext(),
                        getChildFragmentManager(),
                        teacher
                );
            }

            @Override
            public void onSendMessage(Teacher teacher) {
                String teacherId = teacher.getId();
                if (teacherId == null || teacherId.isEmpty()) {
                    Toast.makeText(requireContext(), "Erreur : L'ID de l'enseignant est introuvable", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lancer ChatActivity avec les informations nécessaires
                Intent intent = new Intent(requireContext(), ChatActivity.class);
                intent.putExtra("TEACHER_ID", teacherId); // Transmettre l'ID du professeur
                intent.putExtra("TEACHER_NAME", teacher.getFullName()); // Optionnel : Nom du professeur
                requireContext().startActivity(intent);
            }

            @Override
            public void onAddReview(Teacher teacher) {

                User student = mainViewModel.getCurrentUser().getValue();
                if (student == null) {
                    Toast.makeText(requireContext(), "Error: User not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!(student instanceof Student)) {
                    Toast.makeText(requireContext(), "Teachers cannot rate other teachers", Toast.LENGTH_SHORT).show();
                    return;
                }
                Student s = (Student) student;
                RatingDialog.showDialog((rating, comment) -> mainViewModel.addComment(comment, rating, teacher, s, new OnDataCallback<Void>() {
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
                        .addOnSuccessListener(unused -> {
                            Snackbar.make(binding.getRoot(), "Teacher removed from favorites", Snackbar.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> {
                            Snackbar.make(binding.getRoot(), "Failed to remove teacher from favorites", Snackbar.LENGTH_LONG).show();
                        });
            }
        });

        ////////////////////////////////////////
        // 🔹 Ici on met l’observer pour afficher tous les enseignants au chargement
        mainViewModel.getTeachers().observe(getViewLifecycleOwner(), teachers -> {
            // Affiche tous les enseignants si aucun filtre n’est sélectionné
            if (selectedCategory[0] == null || selectedCategory[0].isEmpty()) {
                adapter.setTeacherData(teachers);
            }
        });

        /////////////////////////////////////////

        mainViewModel.getMyFavorites()
                .observe(getViewLifecycleOwner(), teachers -> adapter.setFavorites(new HashSet<>(teachers)));

        binding.rvTeachers.setAdapter(adapter);

        binding.viewMoreBtn.setOnClickListener(viewMoreBtn -> {
            if (binding.filterLayout.getVisibility() == View.GONE) {
                binding.filterLayout.setVisibility(View.VISIBLE);
                binding.viewMoreBtn.setText("View less"); // Texte en dur pour "Voir moins"
            } else {
                binding.filterLayout.setVisibility(View.GONE);
                binding.viewMoreBtn.setText("View more"); // Texte en dur pour "Voir moins"
            }
        });

        // Ajout de l'action pour le bouton applyFilterButton
        binding.applyFilterBtn.setOnClickListener(v -> {
            if (selectedCategory[0] == null) {
                Snackbar.make(binding.getRoot(), "Please select a category first!", Snackbar.LENGTH_LONG).show();
                return;
            }

            // Récupération des valeurs de minPrice et maxPrice
            String minPriceText = binding.minPrice.getText().toString();
            String maxPriceText = binding.maxPrice.getText().toString();

            // Conversion des valeurs en double
            double minPrice = minPriceText.isEmpty() ? Double.MIN_VALUE : Double.parseDouble(minPriceText);
            double maxPrice = maxPriceText.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPriceText);

            // Effectuer le filtrage
            mainViewModel.filterTeachers(selectedCategory[0], minPrice, maxPrice);

            mainViewModel.getTeacherSearchResults()
                    .observe(getViewLifecycleOwner(), teachers -> adapter.setTeacherData(teachers));
        });

        // Gestion du bouton retour
        binding.backBtn.setOnClickListener(v -> findNavController().popBackStack());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
