package com.example.booking_movie.utils;

public class SecurityUtils {
    public static String generateRandomOtp() {
        int otp = (int)(Math.random() * 1000000);
        return String.format("%06d", otp);
    }
}
