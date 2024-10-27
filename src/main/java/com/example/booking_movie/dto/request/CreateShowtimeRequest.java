package com.example.booking_movie.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

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

    @NotNull(message = "End time not be null")
    LocalTime endTime;

    Integer totalSeat;

    Integer emptySeat;

    @NotNull(message = "Status must not be null")
    String status;

    @NotNull(message = "Movie Id must not be null")
    String movieId;
    String theaterId;
}
