package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

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
    String content;
    Integer duration;
    Double rate;
    String image;
    Boolean canComment;
    List<GenreResponse> genres;
}
