package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieDetailResponse {
    String id;
    String name;
    String premiere;
    String language;
    Integer duration;
    String content;
    Double rate;
    String image;
    Set<GenreResponse> genres;
    PersonResponse director;
    Set<PersonResponse> actors;
}
