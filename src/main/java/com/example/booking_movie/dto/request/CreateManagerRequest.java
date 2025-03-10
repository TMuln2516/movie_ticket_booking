package com.example.booking_movie.dto.request;

import com.example.booking_movie.validator.PasswordConstrain;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateManagerRequest {
    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 10, message = "Username must have at least 3 characters")
    String username;

    @PasswordConstrain
    String password;

    @NotNull(message = "First Name must not be null")
    String firstName;

    @NotNull(message = "Last Name must not be null")
    String lastName;

}
