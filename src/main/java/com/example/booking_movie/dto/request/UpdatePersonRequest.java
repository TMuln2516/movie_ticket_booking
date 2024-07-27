package com.example.booking_movie.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePersonRequest {
    String name;
    Boolean gender;
    Date dateOfBirth;
    String job;
    String image;
}
