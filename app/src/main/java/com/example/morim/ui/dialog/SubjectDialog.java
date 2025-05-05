package com.example.morim.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.morim.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SubjectDialog {

    public static void showSubjectDialog(Context context, OnSuccessListener<String> onSubjectSelected) {
        FrameLayout container = new FrameLayout(context);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 16;
        params.rightMargin = 16;

        TextInputLayout textInputLayout = new TextInputLayout(context);
        TextInputEditText textInputEditText = new TextInputEditText(textInputLayout.getContext());

        textInputLayout.setLayoutParams(params);
        textInputLayout.addView(textInputEditText);

        container.addView(textInputLayout);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Choose meeting subject")
                .setMessage("Please enter subject for the meeting")
                .setView(container)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    String inputText = textInputEditText.getText().toString();
                    if (inputText.isEmpty()) {
                        Toast.makeText(context, "Subject left empty! defaulting to 'will discuss with teacher'", Toast.LENGTH_SHORT).show();
                    }
                    onSubjectSelected.onSuccess(inputText);
                })
                .setNegativeButton("Cancel", null)  // Set the Cancel button
                .create();
        dialog.show();
    }
}
