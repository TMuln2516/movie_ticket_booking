package com.example.booking_movie.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateShowtimeRequest {
    @NotNull(message = "Date must not be null")
    LocalDate date;

    @NotNull(message = "Start time must not be null")
    LocalTime startTime;

    @NotNull(message = "Movie Id must not be null")
    String movieId;

    @NotNull(message = "Room Id must not be null")
    String roomId;
}
