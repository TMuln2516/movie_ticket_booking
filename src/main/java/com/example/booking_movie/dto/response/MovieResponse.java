package com.example.booking_movie.dto.response;

import com.example.booking_movie.entity.Person;
import com.example.booking_movie.entity.Genre;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieResponse {
    String id;
    String name;
    String premiere;
    String language;
    Integer duration;
    Double rate;
    String image;
}
