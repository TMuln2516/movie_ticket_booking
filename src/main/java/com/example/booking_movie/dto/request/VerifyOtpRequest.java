package com.example.booking_movie.dto.request;

import com.example.booking_movie.validator.EmailConstrain;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerifyOtpRequest {
    String otp;

    @EmailConstrain
    String email;
}
