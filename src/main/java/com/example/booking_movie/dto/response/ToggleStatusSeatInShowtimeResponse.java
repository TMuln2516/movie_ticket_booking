package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ToggleStatusSeatInShowtimeResponse {
    String id;
    Integer status;
    SeatResponse seat;
    ShowtimeResponse showtime;
}
