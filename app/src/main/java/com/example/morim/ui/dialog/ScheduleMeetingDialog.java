package com.example.morim.ui.dialog;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;

import com.example.morim.components.TeacherCalendar;
import com.example.morim.databinding.FragmentMeetingSchduleBinding;
import com.example.morim.model.Meeting;
import com.example.morim.model.Teacher;
import com.example.morim.util.DateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleMeetingDialog extends DialogFragment implements TeacherCalendar.OnDateSelectedListener {
    private Teacher teacher;
    private List<Meeting> meetings;

    public ScheduleCompleteListener scheduleCompleteListener;

    public interface ScheduleCompleteListener {
        void onScheduleComplete(LocalDateTime date);
    }

    public ScheduleMeetingDialog(List<Meeting> meetings,
                                 Teacher teacher,
                                 ScheduleCompleteListener listener) {
        this.meetings = meetings;
        this.teacher = teacher;
        this.scheduleCompleteListener = listener;
    }

    private FragmentMeetingSchduleBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMeetingSchduleBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TeacherCalendar calendar = new TeacherCalendar(
                getContext(),
                new ArrayList<>(),
                this
        );
        binding.getRoot().addView(calendar);
    }

    @Override
    public void onDateSelected(LocalDate date) {
        int day = date.getDayOfMonth();
        Calendar c = Calendar.getInstance();
        List<Meeting> meetingsAtSelectedDay = meetings.stream().filter(
                meeting -> {
                    c.setTimeInMillis(meeting.getMeetingDate());
                    return day == c.get(Calendar.DAY_OF_MONTH);
                }
        ).collect(Collectors.toList());

        // Calendar Full 8 AM - 8 PM
        if (meetingsAtSelectedDay.size() == 12) {
            Toast.makeText(requireContext(), "The teacher is full at this date", Toast.LENGTH_LONG).show();
        }

        c.setTimeInMillis(System.currentTimeMillis());
        new HourDialog(date, meetingsAtSelectedDay, teacher, hour -> {
            scheduleCompleteListener.onScheduleComplete(DateUtils.dateAtHour(date, hour));
            dismiss();
        }).show(getChildFragmentManager(), "Hour Dialog");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scheduleCompleteListener = null;
    }
}