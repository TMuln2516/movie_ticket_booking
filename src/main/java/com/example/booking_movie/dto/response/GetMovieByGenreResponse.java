package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetMovieByGenreResponse {
    String id;
    String name;
    List<MovieDetailResponse> listMovies;
}
