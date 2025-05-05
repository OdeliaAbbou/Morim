package com.example.morim.components;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.morim.util.DateUtils;
import com.example.morim.util.ScreenUtils;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class NACalendarView extends GridLayout {



    private final int screenWidth;

    class CalendarDate {
        private LocalDate date;
        private boolean selected;
        private TextView textView;

        public CalendarDate(LocalDate date, TextView textView, boolean selected) {
            this.date = date;
            this.selected = selected;
            this.textView = textView;
            textView.setLayoutParams(new LinearLayout.LayoutParams(screenWidth / 7, WRAP_CONTENT));
            textView.setPadding(8, 32, 8, 32);
            textView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            textView.setText(String.valueOf(date.getDayOfMonth()));
        }


        public TextView getTextView() {
            return textView;
        }

        public LocalDate getDate() {
            return date;
        }
    }

    protected List<CalendarDate> calendarDays;
    protected final Set<LocalDate> unavailableDates;

    protected NACalendarView(Context context) {
        super(context);
        unavailableDates = new HashSet<>();
        screenWidth = ScreenUtils.getScreenSize(context).x;
    }

    private LocalDate currentDate = LocalDate.now();

    public NACalendarView(Context context,
                          List<LocalDate> unavailableDates) {
        super(context);
        this.unavailableDates = new HashSet<>(unavailableDates);
        screenWidth = ScreenUtils.getScreenSize(context).x;
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                spec(1, 1),
                spec(1, 1));
        params.setGravity(Gravity.CENTER);
        setLayoutParams(params);
        setColumnCount(7);
        setRowCount(7);

        // Initialize current date
        currentDate = LocalDate.now();

        // Add navigation bar to control the calendar
        addNavigationBar(context);

        createWeekDatesWithRespectTo(currentDate);
        renderCalendarDays();
    }

    private void addNavigationBar(Context context) {
        // Create a LinearLayout to hold the navigation buttons
        LinearLayout navigationBar = new LinearLayout(context);
        navigationBar.setOrientation(LinearLayout.HORIZONTAL);
        navigationBar.setGravity(Gravity.CENTER);
        navigationBar.setLayoutParams(new GridLayout.LayoutParams(
                spec(0, 1), // Row 0
                spec(0, 7)  // Span all 7 columns
        ));

        // Define layout parameters for buttons
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                0, // Width (weight-based)
                LinearLayout.LayoutParams.WRAP_CONTENT, // Height
                1  // Weight (evenly distributes)
        );
        buttonParams.setMargins(10, 10, 10, 10); // Add margins for spacing

        // Define layout for the month-year label
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2  // Give more weight to text view for better spacing
        );
        labelParams.setMargins(10, 10, 10, 10);

        Button prevYearButton = new Button(context);
        prevYearButton.setText("◀◀");
        prevYearButton.setLayoutParams(buttonParams);
        prevYearButton.setOnClickListener(v -> changeYear(-1));

        Button prevMonthButton = new Button(context);
        prevMonthButton.setText("◀");
        prevMonthButton.setLayoutParams(buttonParams);
        prevMonthButton.setOnClickListener(v -> changeMonth(-1));

        // Display for the current month and year
        TextView monthYearLabel = new TextView(context);
        monthYearLabel.setTextSize(18);
        monthYearLabel.setText(currentDate.getMonth().toString() + " " + currentDate.getYear());
        monthYearLabel.setGravity(Gravity.CENTER);
        monthYearLabel.setLayoutParams(labelParams);

        Button nextMonthButton = new Button(context);
        nextMonthButton.setText("▶");
        nextMonthButton.setLayoutParams(buttonParams);
        nextMonthButton.setOnClickListener(v -> changeMonth(1));

        Button nextYearButton = new Button(context);
        nextYearButton.setText("▶▶");
        nextYearButton.setLayoutParams(buttonParams);
        nextYearButton.setOnClickListener(v -> changeYear(1));

        // Add all elements to the navigation bar
        navigationBar.addView(prevYearButton);
        navigationBar.addView(prevMonthButton);
        navigationBar.addView(monthYearLabel);
        navigationBar.addView(nextMonthButton);
        navigationBar.addView(nextYearButton);

        // Add navigation bar to the layout
        addView(navigationBar);
    }


    // Methods to update the calendar when changing month or year
    private void changeMonth(int delta) {
        currentDate = currentDate.plusMonths(delta);
        updateCalendar();
    }

    private void changeYear(int delta) {
        currentDate = currentDate.plusYears(delta);
        updateCalendar();
    }

    private void updateCalendar() {
        removeAllViews();
        addNavigationBar(getContext());
        createWeekDatesWithRespectTo(currentDate);
        renderCalendarDays();
    }


    public void createWeekDatesWithRespectTo(LocalDate ref) {
        calendarDays = DateUtils.monthDates(ref)
                .stream()
                .map(date -> new CalendarDate(date, new TextView(getContext()), false))
                .collect(Collectors.toList());
    }

    private void renderCalendarDays() {
        for (int day = 1; day <= 7; ++day) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth / 7, WRAP_CONTENT);
            TextView tvDay = new TextView(getContext());
            tvDay.setPadding(8, 64, 8, 16);
            tvDay.setLayoutParams(params);
            tvDay.setTypeface(null, Typeface.BOLD);
            tvDay.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            tvDay.setTextSize(12);
            tvDay.setText(DateUtils.getDayByInteger(day));
            addView(tvDay);
        }
        for (CalendarDate date : calendarDays) {
            addView(date.getTextView());
        }

    }

}
