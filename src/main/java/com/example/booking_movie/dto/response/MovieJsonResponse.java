package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieJsonResponse {
    String id;
    String name;
    String premiere;
    String language;
    String content;
    Integer duration;
    Double rate;
    String image;
    String publicId;
    LocalDate createAt;
    Set<String> genreIds;
    Set<String> personIds;
    Set<String> showtimeIds;
    Set<String> feedbackIds;
}
