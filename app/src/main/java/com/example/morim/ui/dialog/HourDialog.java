package com.example.morim.ui.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.morim.model.Meeting;
import com.example.morim.model.Teacher;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class HourDialog extends DialogFragment {
    private LocalDate date;
    private int selectedHour = -1;
    private final Teacher teacher;
    private final List<Meeting> meetings;

    private HourListener hourListener;


    public interface HourListener {
        void onHourSelected(int hour);
    }

    public HourDialog(LocalDate date, List<Meeting> meeting, Teacher teacher, HourListener listener) {
        this.date = date;
        this.meetings = meeting;
        this.teacher = teacher;
        this.hourListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        GridLayout gLayout = new GridLayout(getContext());
        gLayout.setPadding(32, 32, 32, 32);
        GridLayout.LayoutParams gLayoutParams = new GridLayout.LayoutParams();
        gLayoutParams.columnSpec = GridLayout.spec(1, 1);
        gLayoutParams.rowSpec = GridLayout.spec(1, 1);
        gLayoutParams.setGravity(Gravity.CENTER);
        gLayout.setColumnCount(2);
        gLayout.setRowCount(8);
        gLayout.setLayoutParams(gLayoutParams);

        LinearLayout layoutAm = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(350, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        layoutAm.setOrientation(LinearLayout.VERTICAL);
        params.gravity = Gravity.CENTER;
        layoutAm.setLayoutParams(params);

        LinearLayout layoutPm = new LinearLayout(getContext());
        layoutPm.setOrientation(LinearLayout.VERTICAL);
        params.gravity = Gravity.CENTER;
        layoutPm.setLayoutParams(params);

        gLayout.addView(layoutAm);
        gLayout.addView(layoutPm);
        List<TextView> allHoursTvs = new ArrayList<>();
        HashMap<Integer, Meeting> meetingsByHour = new HashMap<>();
        Calendar c = Calendar.getInstance();
        for (Meeting m : meetings) {
            c.setTimeInMillis(m.getMeetingDate());
            int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
            meetingsByHour.put(hourOfDay, m);
        }
        LocalDate now = LocalDate.now();
        boolean insertedAtLeast1 = false;
        for (int i = 0; i <= 15; i++) {
            // check if the hour is in the past and the date is today
            if ((now.getDayOfMonth()
                    == date.getDayOfMonth()
                    && now.getMonth() == date.getMonth()
                    && now.getYear() == date.getYear())
//                    && i + 8 < c.get(Calendar.HOUR_OF_DAY))
                    && (i + 8) < Calendar.getInstance().get(Calendar.HOUR_OF_DAY))

                continue;
            insertedAtLeast1 = true;

            TextView hourTv = new TextView(getContext());
            hourTv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            hourTv.setPadding(16, 16, 16, 16);

//            if ((i + 8) <= 12) {
//                hourTv.setText(Math.abs(i + 8) + ":00 AM");
//                layoutAm.addView(hourTv);
//            } else {
//                hourTv.setText((i + 8) + ":00 PM");
//                layoutPm.addView(hourTv);
//            }
            ////////////////
            int hour = i + 8;

// Texte de l’heure
            if (hour <=12) {
                hourTv.setText(hour + ":00 AM");

            } else {
                hourTv.setText((hour) + ":00 PM");
            }

// Placement gauche/droite selon la plage
            if (hour >= 8 && hour <= 15) {
                layoutAm.addView(hourTv); // 8 à 15 à gauche
            } else {
                layoutPm.addView(hourTv); // 16 à 23 à droite
            }

            //////////////////
            allHoursTvs.add(hourTv);
            final int cI = i;
            if (meetingsByHour.containsKey(i + 8)) {
                hourTv.setBackgroundColor(Color.LTGRAY);
                hourTv.setEnabled(false);

            } else
                hourTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < allHoursTvs.size(); i++) {
                            if (meetingsByHour.containsKey(i + 8)) {
                                continue;
                            }
                            TextView other = allHoursTvs.get(i);
                            if (!other.equals(hourTv)) {
                                other.setBackgroundColor(Color.WHITE);
                                other.setTypeface(null, Typeface.NORMAL);
                                other.setTextColor(Color.BLACK);
                            }
                            selectedHour = cI + 8;
                            hourTv.setTypeface(null, Typeface.BOLD);
                            hourTv.setBackgroundColor(Color.GRAY);
                            hourTv.setTextColor(Color.WHITE);
                        }
                    }
                });
        }

        if(!insertedAtLeast1) {
            return new AlertDialog.Builder(getContext())
                    .setTitle("No available hours")
                    .setMessage("The teacher has no available hours lefts at this date.")
                    .setPositiveButton("OK", null)
                    .create();
        }

        return new AlertDialog.Builder(getContext())
                .setTitle("Schedule with " + teacher.getFullName())
                .setView(gLayout)
                .setPositiveButton("Schedule", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (selectedHour != -1)
                            hourListener.onHourSelected(selectedHour);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hourListener = null;
    }
}