package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMovieResponse {
    String id;
    String name;
    String premiere;
    String language;
    Integer duration;
    String content;
    Double rate;
    String image;
}
