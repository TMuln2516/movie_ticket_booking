package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetMovieByPersonResponse {
    String id;
    String name;
    Boolean gender;
    String dateOfBirth;
    String image;
    JobResponse job;
    List<MovieDetailResponse> listMovies;
}
