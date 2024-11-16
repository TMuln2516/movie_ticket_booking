package com.example.booking_movie.dto.request;

import com.example.booking_movie.validator.EmailConstrain;
import com.example.booking_movie.validator.PasswordConstrain;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePasswordRequest {
    @PasswordConstrain
    String password;
}
