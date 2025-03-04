package com.example.booking_movie.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return date.format(formatter);
    }

    public static String formatTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return time.format(formatter);
    }

    public static LocalDate epochToLocalDate(Long epochMillis) {
        return Instant.ofEpochMilli(epochMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
    public static Long formatDateToEpochMillis(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    // Hàm chuyển đổi từ String sang LocalDate
    public static LocalDate formatStringToLocalDate(String dateStr, String pattern) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(dateStr, dateFormatter);
    }

    // Hàm chuyển đổi từ String sang LocalTime
    public static LocalTime formatStringToLocalTime(String timeStr, String pattern) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(pattern);
        return LocalTime.parse(timeStr, timeFormatter);
    }
}
