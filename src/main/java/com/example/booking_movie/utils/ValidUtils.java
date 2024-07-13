package com.example.booking_movie.utils;

import java.util.function.Consumer;

public class ValidUtils {
    //    function check null field before update
    public static  <T> void updateFieldIfNotEmpty(Consumer<T> setter, T value) {
//        check value not null + value is String and value not empty
        if (value != null && !(value instanceof String && ((String) value).isEmpty())) {
            setter.accept(value);
        }
    }
}
