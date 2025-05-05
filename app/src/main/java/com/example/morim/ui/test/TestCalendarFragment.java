package com.example.morim.ui.test;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.morim.components.NACalendarView;
import com.example.morim.components.TeacherCalendar;
import com.example.morim.databinding.FragmentTestCalendarBinding;
import com.example.morim.ui.BaseFragment;
import com.google.common.collect.Lists;

import java.time.LocalDate;
import java.util.ArrayList;

public class TestCalendarFragment extends BaseFragment implements TeacherCalendar.OnDateSelectedListener {

    private FragmentTestCalendarBinding viewBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentTestCalendarBinding.inflate(inflater);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NACalendarView calendarView = new TeacherCalendar(
                requireContext(),
                Lists.newArrayList(LocalDate.now()),
                this
        );
        viewBinding.getRoot().addView(calendarView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewBinding = null;
    }

    @Override
    public void onDateSelected(LocalDate date) {
        Log.d("onDateSelected", date.toString());

    }
}
