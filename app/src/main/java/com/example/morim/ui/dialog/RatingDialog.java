package com.example.morim.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.morim.databinding.RatingDialogBinding;

public class RatingDialog extends DialogFragment {


    public interface RatingDialogListener {
        void onRatingSubmit(float rating, String comment);
    }

    public static final String TAG = "RatingDialog";
    private final RatingDialogListener listener;

    public RatingDialog(RatingDialogListener listener) {
        this.listener = listener;
    }

    private RatingDialogBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = RatingDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.submitReviewButton.setOnClickListener(v -> {
            binding.reviewTextInput.setError(null);
            if (binding.reviewTextInput.getText().toString().isEmpty()) {
                binding.reviewTextInput.setError("Please enter a review");
                return;
            }
            if (binding.ratingBar.getRating() == 0) {
                // be kind to the user and assume they meant to give a 1 star rating
                binding.ratingBar.setRating(1);
            }
            listener.onRatingSubmit(binding.ratingBar.getRating(), binding.reviewTextInput.getText().toString());
            dismiss();
        });
        binding.cancelButton.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public static void showDialog(RatingDialogListener listener, FragmentManager fragmentManager) {
        RatingDialog dialog = new RatingDialog(listener);
        dialog.show(fragmentManager, TAG);
    }
}
