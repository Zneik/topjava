package ru.javawebinar.topjava.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static <T extends Comparable<T>> boolean isBetweenHalfOpen(T lt, T start, T end) {
//        return lt.compareTo(startTime) >= 0 && lt.compareTo(endTime) < 0;
        boolean result = true;
        if (start != null && !(lt.compareTo(start) >= 0)) {
            result = false;
        }
        if (end != null && !(lt.compareTo(end) < 0)) {
            result = false;
        }
        return result;
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }

    public static LocalTime parseTime(String time) {
        return time != null && !time.isEmpty() ? LocalTime.parse(time) : null;
    }

    public static LocalDate parseDate(String date) {
        return date != null && !date.isEmpty() ? LocalDate.parse(date) : null;
    }
}

