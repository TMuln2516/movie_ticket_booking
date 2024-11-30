package com.example.booking_movie.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateFeedbackRequest {
    @NotNull
    String content;

    @NotNull
    Double rate;

    @NotNull
    LocalDate date;

    @NotNull
    LocalTime time;
}
