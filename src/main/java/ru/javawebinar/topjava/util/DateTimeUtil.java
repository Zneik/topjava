package ru.javawebinar.topjava.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static <T extends Comparable<T>> boolean isBetweenHalfOpen(T lt, T start, T end) {
        return (start == null || lt.compareTo(start) >= 0) && (end == null || lt.compareTo(end) < 0);
    }

    public static <T extends Comparable<T>> boolean isBetweenInclusive(T lt, T start, T end) {
        return (start == null || lt.compareTo(start) >= 0) && (end == null || lt.compareTo(end) <= 0);
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

    public static LocalDate addDayToDate(LocalDate date) {
        return date != null ? date.plusDays(1) : null;
    }

}

