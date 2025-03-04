package com.example.booking_movie.dto.request;

import com.example.booking_movie.validator.PasswordConstrain;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePasswordRequest {
    @PasswordConstrain
    String password;
}
