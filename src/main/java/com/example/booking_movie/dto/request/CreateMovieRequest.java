package com.example.booking_movie.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMovieRequest {
    @NotNull(message = "Name must not be null")
    String name;

    @NotNull(message = "Premiere Day must not be null")
    @Future(message = "Invalid date")
    LocalDate premiere;

    @NotNull(message = "Language must not be null")
    String language;

    @Min(value = 60, message = "Duration must be at least 60 minutes")
    Integer duration;

    @NotNull(message = "Content must not be null")
    String content;

    Double rate;

    @NotEmpty
    Set<String> genresId;

    @NotNull
    String directorId;

    @NotEmpty
    Set<String> actorsId;
}
