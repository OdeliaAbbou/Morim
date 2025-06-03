package com.example.morim.ui.dialog;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.morim.MorimApp;
import com.example.morim.R;
import com.example.morim.databinding.FragmentTeacherDetailsBinding;
import com.example.morim.model.Location;
import com.example.morim.model.Teacher;
import com.example.morim.ui.SubjectSpinner;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import dagger.hilt.android.AndroidEntryPoint;
import im.delight.android.location.SimpleLocation;

public class TeacherDetailsDialog extends DialogFragment {

    private OnDetailsSelected onDetailsSelected;

    public TeacherDetailsDialog(OnDetailsSelected onDetailsSelected) {
        this.onDetailsSelected = onDetailsSelected;
        editedTeacher = null;
    }
    private final Teacher editedTeacher;
    public TeacherDetailsDialog(Teacher editedTeacher,OnDetailsSelected onDetailsSelected) {
        this.onDetailsSelected = onDetailsSelected;
        this.editedTeacher = editedTeacher;
    }

    public interface OnDetailsSelected {
        void onDetailsSelected(List<String> teachingSubjects,
                               String teachingArea,
                               Location teachingLocation,
                               String education,
                               double price);
    }

    private final Set<String> selectedSubjects = new HashSet<>();
    private SimpleLocation simpleLocation;
    private String teachingArea;
    private String education;
    private Location teachingLocation;
    private TextView selectedSubjectV;
    private FragmentTeacherDetailsBinding binding;

    private final SubjectSpinner.OnItemSelected onSubjectSelected = new SubjectSpinner.OnItemSelected() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (i == 0) return;
            String subject = MorimApp.ALL_SUBJECTS.get(i);
            if (selectedSubjects.contains(subject))
                selectedSubjects.remove(subject);
            else
                selectedSubjects.add(subject);
            setSelectedSubjects();
        }
    };

    public void populateWithPredefinedData(String education, String teachingArea, Location teachingLocation, double price, List<String> subjects) {
        this.education = education;
        this.teachingArea = teachingArea;
        this.teachingLocation = teachingLocation;
        this.selectedSubjects.addAll(subjects);
        binding.etEducationDetails.setText(education);
        binding.etPrice.setText(String.valueOf(price));
        setSelectedSubjects();
    }

    private void populateWithPredefinedData(Teacher teacher) {
        populateWithPredefinedData(teacher.getEducation(),
                teacher.getTeachingArea(),
                teacher.getTeachingLocation(),
                teacher.getPrice(),
                teacher.getTeachingSubjects()
        );
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTeacherDetailsBinding.inflate(inflater);
        simpleLocation = new SimpleLocation(requireContext());
        if (simpleLocation.hasLocationEnabled()) {
            simpleLocation.beginUpdates();
        }
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Spinner spinner = new SubjectSpinner(getContext(), onSubjectSelected);
        spinner.setOnTouchListener((v, motionEvent) -> {
            spinner.setSelection(0);
            spinner.performClick();
            return true;
        });
        spinner.setPadding(16, 0, 16, 0);
        binding.subjectSpinnerLayout.addView(spinner);
        selectedSubjectV = new TextView(getContext());
        selectedSubjectV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        selectedSubjectV.setTypeface(null, Typeface.BOLD);
        selectedSubjectV.setPadding(8, 8, 8, 8);
        selectedSubjectV.setTextSize(14);
        selectedSubjectV.setMaxWidth(200);


        binding.subjectSpinnerLayout.addView(selectedSubjectV);
        binding.btnDismissDialog.setOnClickListener(v -> dismiss());
        if (teachingArea != null) {
            binding.selectedCity.setText("Selected city: " + teachingArea);
        }

        if (education != null) {
            binding.etEducationDetails.setText(education);
        }
        Objects.requireNonNull(getDialog()).setOnShowListener(dialogInterface ->
                binding.btnSaveChangesDialog.setOnClickListener(v -> {
                    String priceText = binding.etPrice.getText().toString();
                    double price = Double.parseDouble(binding.etPrice.getText().toString());
                    if (priceText.isEmpty()) {
                        Snackbar.make(binding.getRoot(), "Hourly price must be picked!", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (teachingArea == null || teachingArea.isEmpty()) {
                        Snackbar.make(binding.getRoot(), "Teaching area must be picked!", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    education = binding.etEducationDetails.getText().toString();
                    if (education.isEmpty()) {
                        binding.etEducationDetailsLayout.setError("Education field must be filled!");
                        return;
                    }
                    dismiss();
                    onDetailsSelected.onDetailsSelected(
                            new ArrayList<>(selectedSubjects),
                            teachingArea,
                            teachingLocation,
                            education,
                            price
                    );
                }));
        setSelectedSubjects();

        setupLocationAutoComplete();

        if (editedTeacher != null) {
            populateWithPredefinedData(editedTeacher);
        }
    }

    public void setListener(OnDetailsSelected listener) {
        this.onDetailsSelected = listener;
        if (getDialog() == null) return;
    }

    private void setSelectedSubjects() {
        if (selectedSubjects.isEmpty()) return;
        StringBuilder all = new StringBuilder();
        List<String> subjects = new ArrayList<>(selectedSubjects);
        for (int j = 0; j < selectedSubjects.size(); j++) {
            all.append(subjects.get(j));
            if (j < selectedSubjects.size() - 1)
                all.append(", ");
        }
        if (selectedSubjectV != null)
            selectedSubjectV.setText("Selected Subjects: " + all + "\n(Choose subject again to deselect)");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        onDetailsSelected = null;
        simpleLocation.endUpdates();
    }

    private void setupLocationAutoComplete() {
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        assert autocompleteFragment != null;
        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS_COMPONENTS);
        autocompleteFragment.setPlaceFields(fields);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    double latitude = latLng.latitude;
                    double longitude = latLng.longitude;
                    TeacherDetailsDialog.this.teachingLocation = new Location(latitude, longitude);
                } else {
                    Toast.makeText(requireContext(), "The location service was unable to determine coordinates for selected city, using your current location for search services", Toast.LENGTH_LONG).show();
                    TeacherDetailsDialog.this.teachingLocation = new Location(
                            simpleLocation.getLatitude(),
                            simpleLocation.getLongitude());
                }
                // Handle the selected place
                // Here, process the address components to extract the city
                for (AddressComponent component : place.getAddressComponents().asList()) {
                    for (String type : component.getTypes()) {
                        if (type.equals("locality")) {
                            teachingArea = component.getName();
                            binding.selectedCity.setText("Selected city: " + teachingArea);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle the error
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

}
