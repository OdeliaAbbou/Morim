package com.example.morim.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.lifecycle.Observer;

import com.example.morim.R;
import com.example.morim.adapter.MeetingAdapter;
import com.example.morim.adapter.TeacherAdapter;
import com.example.morim.databinding.FragmentTeachersByLocationBinding;
import com.example.morim.databinding.TeacherItemBinding;
import com.example.morim.model.Favorite;
import com.example.morim.model.MyMeetingsData;
import com.example.morim.model.Student;
import com.example.morim.model.Teacher;
import com.example.morim.model.User;
import com.example.morim.ui.BaseFragment;
import com.example.morim.viewmodel.MainViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import im.delight.android.location.SimpleLocation;

@AndroidEntryPoint
public class TeachersByLocation extends BaseFragment implements OnMapReadyCallback {
    @Inject
    protected SimpleLocation simpleLocation;

    @Inject
    protected ScheduledThreadPoolExecutor executor;

    private FragmentTeachersByLocationBinding binding;
    private MainViewModel mainViewModel;
    private LatLng lastLocation;
    private GoogleMap googleMap;
    private ScheduledFuture<?> mapFuture;


    private final ActivityResultLauncher<String[]> mPermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> o) {
            boolean isAllConfirmed = !o.containsValue(false);
            if (!isAllConfirmed)
                Toast.makeText(requireContext(), "Location permissions were not granted, could not determine your current location..", Toast.LENGTH_LONG).show();
            else {
                simpleLocation.beginUpdates();
                moveToCurrentLocation();
            }
        }
    });


    private boolean requestLocationPermissions() {
        if (requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED ||
                requireActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_DENIED) {
            mPermissions.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
            return false;
        }
        return true;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTeachersByLocationBinding.inflate(inflater, container, false);
        mainViewModel = activityScopedViewModel(MainViewModel.class);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);


        binding.toCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (simpleLocation.hasLocationEnabled() && requestLocationPermissions() && googleMap != null) {
                    moveCameraToCurrent();
                }
            }
        });

        if (simpleLocation.hasLocationEnabled() && requestLocationPermissions())
            simpleLocation.beginUpdates();

        if (supportMapFragment != null)
            supportMapFragment.getMapAsync(this);

        binding.backBtn.setOnClickListener(v -> findNavController().popBackStack());

        mapFuture = executor.scheduleAtFixedRate(() -> {
            LatLng curr = currentLocation();
            if (googleMap != null && simpleLocation.hasLocationEnabled() && !curr.equals(lastLocation)) {
                lastLocation = curr;
                moveCameraToCurrent();
            }
        }, 0, 20, TimeUnit.SECONDS);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        simpleLocation.endUpdates();
        mapFuture.cancel(true);
    }

    @SuppressLint({"PotentialBehaviorOverride"})
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        Map<Marker, User> markers = new HashMap<>();
        mainViewModel.getTeachers()
                .observe(this, teachers -> {
                    for (Teacher teacher : teachers) {
                        MarkerOptions opts = new MarkerOptions()
                                .position(teacher.getTeachingLocation().toLatLng())
                                .title(teacher.getFullName());
                        try {
                            Bitmap bm = Picasso.get()
                                    .load(teacher.getImage())
                                    .get();
                            opts = opts.icon(BitmapDescriptorFactory.fromBitmap(bm));
                        } catch (Exception ignored) {
                        }
                        markers.put(googleMap.addMarker(opts), teacher);
                    }
                });

        if (simpleLocation.hasLocationEnabled()) {
            lastLocation = new LatLng(simpleLocation.getLatitude(),
                    simpleLocation.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(lastLocation, 12));
            Marker currMarker = googleMap.addMarker(
                    new MarkerOptions()
                            .title("Me")
                            .position(new LatLng(lastLocation.latitude, lastLocation.longitude))

            );
            markers.put(currMarker, mainViewModel.getCurrentUser().getValue());
        }
        googleMap.setOnMarkerClickListener((marker) -> {
            User user = markers.get(marker);


            if (user == null) {
                Snackbar.make(binding.getRoot(), "Teacher Not available", Snackbar.LENGTH_LONG).show();
                return true;
            }
            if (!(user instanceof Teacher) || user.getId().equals(FirebaseAuth.getInstance().getUid())) {
                Snackbar.make(binding.getRoot(), "This is yourself (:", Snackbar.LENGTH_LONG).show();
                return true;
            }
            TeacherAdapter.TeacherViewHolder viewHolder = new TeacherAdapter.TeacherViewHolder(
                    TeacherItemBinding.inflate(getLayoutInflater())
            );
            MyMeetingsData data = mainViewModel.getMyMeetingsData().getValue();
            User current = mainViewModel.getCurrentUser().getValue();
            Student student;
            if (current instanceof Student) student = (Student) current;
            else student = null;
            if (data == null) return true;
            List<Favorite> favorites = mainViewModel.getMyFavorites().getValue();
            if (favorites == null) favorites = new ArrayList<>();
            Set<Favorite> favSet = new HashSet<>(favorites);
            viewHolder.bind(student,
                            data.getMyMeetings(),
                            (Teacher) user,
                            favSet,
                            new TeacherAdapter.ScheduleClickListener() {
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
            });
            AlertDialog d = new AlertDialog.Builder(requireContext())
                    .setView(viewHolder.itemView)
                    .show();
            return true;
        });
    }

    private LatLng currentLocation() {
        return new LatLng(simpleLocation.getLatitude(), simpleLocation.getLongitude());
    }


    void moveToCurrentLocation() {
        if (googleMap == null) return;

        if (!simpleLocation.hasLocationEnabled()) {
            Snackbar.make(binding.getRoot(), "Location is disabled. Enable it and try again.", Snackbar.LENGTH_LONG).show();
            return;
        }

        LatLng newLocation = currentLocation();
        if (newLocation.latitude == 0 && newLocation.longitude == 0) {
            Snackbar.make(binding.getRoot(), "Waiting for location update...", Snackbar.LENGTH_LONG).show();
            return;
        }

        lastLocation = newLocation;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 12));
    }

    private void moveCameraToCurrent() {
        requireActivity().runOnUiThread(() -> {
            LatLng currentLatLng = currentLocation();

            if (currentLatLng.latitude == 0 && currentLatLng.longitude == 0) {
                Snackbar.make(binding.getRoot(), "Location not available yet. Please try again.", Snackbar.LENGTH_LONG).show();
                return;
            }

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));
        });
    }
}
