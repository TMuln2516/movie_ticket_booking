package com.example.booking_movie.dto.response;

import com.example.booking_movie.entity.Movie;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonResponse {
    String id;
    String name;
    Boolean gender;
    String dateOfBirth;
    String image;
    JobResponse job;
}
