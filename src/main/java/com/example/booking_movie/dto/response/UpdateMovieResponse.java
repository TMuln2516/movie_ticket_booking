package com.example.booking_movie.dto.response;

import com.example.booking_movie.entity.Genre;
import com.example.booking_movie.entity.Person;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateMovieResponse {
    String id;
    String name;
    String premiere;
    String language;
    Integer duration;
    String content;
    Double rate;
    String image;
    Set<Genre> genres;
    Set<Person> actors;
}
