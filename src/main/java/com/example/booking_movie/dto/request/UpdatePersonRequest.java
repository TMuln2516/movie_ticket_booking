package com.example.booking_movie.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePersonRequest {
    String name;
    Boolean gender;
    LocalDate dateOfBirth;
    String job;
}
