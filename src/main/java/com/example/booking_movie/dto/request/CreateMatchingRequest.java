package com.example.booking_movie.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMatchingRequest {
    @NotNull(message = "Tên phim không được để trống")
    String movieName;

    @NotNull(message = "Thời gian suất chiếu không được để trống")
    LocalDateTime showtime;

    @NotNull(message = "Tên rạp chiếu phim không được để trống")
    String theaterName;
}
