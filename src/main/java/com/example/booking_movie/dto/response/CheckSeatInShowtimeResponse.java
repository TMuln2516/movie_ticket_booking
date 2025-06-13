package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckSeatInShowtimeResponse {
    String id;
    String date;
    String startTime;
    String endTime;
    Integer totalSeat;
    Integer emptySeat;
    Integer bookedSeat;
    String status;
    List<CheckSeatResponse> seats;
}
