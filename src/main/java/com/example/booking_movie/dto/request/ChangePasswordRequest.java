package com.example.booking_movie.dto.request;

import com.example.booking_movie.validator.EmailConstrain;
import com.example.booking_movie.validator.PasswordConstrain;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {
    @EmailConstrain
    String email;
    @PasswordConstrain
    String password;
    String passwordConfirm;
    String otp;
}
