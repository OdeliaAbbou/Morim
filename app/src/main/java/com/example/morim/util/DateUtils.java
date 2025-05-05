package com.example.morim.util;


import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DateUtils {

    public static long dateTimeToEpoch(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return zonedDateTime.toInstant().toEpochMilli();
    }

    public static LocalDateTime dateAtHour(LocalDate date, int hour) {
        return date.atTime(hour, 0);
    }

    public static String formatEpochMillis(long epochMillis) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(epochMillis),
                ZoneId.systemDefault() // Adjust to the system default time zone
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy");
        return dateTime.format(formatter);
    }


    public static List<LocalDate> monthDates(LocalDate ref) {
        LocalDate firstOfMonth = ref.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastOfMonth = ref.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate current = firstOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        final LocalDate last = lastOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        List<LocalDate> dates = new ArrayList<>();
        while (!current.isAfter(last)) {
            LocalDate nextDate = current;
            current = current.plusDays(1);
            dates.add(nextDate);
        }
        return dates;
    }

    public static String getDayByInteger(int day) {
        switch (day) {
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
                return "Thu";
            case 6:
                return "Fri";
            case 7:
                return "Sat";
            default:
                return "N/A";
        }
    }

}
