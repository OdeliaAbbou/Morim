package com.example.morim.components;

import android.content.Context;
import android.graphics.Color;

import java.time.LocalDate;
import java.util.List;

public class TeacherCalendar extends NACalendarView {

    private OnDateSelectedListener dateSelectedListener;

    public interface OnDateSelectedListener {
        void onDateSelected(LocalDate date);
    }

    protected TeacherCalendar(Context context) {
        super(context);
    }

    public TeacherCalendar(Context context,
                           List<LocalDate> unavailableDates,
                           OnDateSelectedListener onDateSelectedListener) {
        super(context, unavailableDates);
        this.dateSelectedListener = onDateSelectedListener;
    }


    @Override
    public void createWeekDatesWithRespectTo(LocalDate ref) {
        super.createWeekDatesWithRespectTo(ref);
        calendarDays.forEach(cd -> {
            boolean isUnavailable = unavailableDates.contains(cd.getDate());
            boolean isPast = cd.getDate().isBefore(LocalDate.now());
            if (!isUnavailable && !isPast) {
                cd.getTextView().setOnClickListener(v -> {
                            dateSelectedListener.onDateSelected(cd.getDate());
                        }
                );
            } else {
                cd.getTextView().setTextColor(Color.GRAY);
                cd.getTextView().setClickable(false);
                cd.getTextView().setFocusable(false);
                cd.getTextView().setTextColor(Color.LTGRAY);
            }
        });

    }
}
