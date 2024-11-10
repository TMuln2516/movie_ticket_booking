package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateShowtimeResponse {
    String id;
    String date;
    String startTime;
    String endTime;
    Integer totalSeat;
    Integer emptySeat;
    String status;
}
