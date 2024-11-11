package com.example.booking_movie.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePersonRequest {
    @NotNull(message = "Name must not be null")
    String name;

    @NotNull(message = "Invalid gender")
    Boolean gender;

    @Past(message = "Invalid Date")
    LocalDate dateOfBirth;
}
