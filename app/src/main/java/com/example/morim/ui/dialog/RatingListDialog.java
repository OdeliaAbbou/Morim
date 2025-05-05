package com.example.morim.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.morim.adapter.CommentAdapter;
import com.example.morim.databinding.RatingDialogBinding;
import com.example.morim.databinding.RatingsListDialogBinding;
import com.example.morim.model.Comment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RatingListDialog extends DialogFragment {

    private List<Comment> ratings;

    public RatingListDialog(List<Comment> ratings) {
        this.ratings = ratings;
    }

    private RatingsListDialogBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = RatingsListDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.ratingsRv.setAdapter(new CommentAdapter(ratings));
        binding.closeButton.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static void showRatingListDialog(Collection<Comment> ratings, FragmentManager fragmentManager) {
        RatingListDialog dialog = new RatingListDialog(new ArrayList<>(ratings));
        dialog.show(fragmentManager, "RatingListDialog");
    }
}
