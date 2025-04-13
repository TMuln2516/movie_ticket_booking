package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketDetailResponse {
    String id;
    String date;
    String time;
    String startTime;
    String dateScreenTime;
    String endTime;
    String movieName;
    String movieId;
    String theaterName;
    String roomName;
    Boolean canComment;
    Set<SeatResponse> seats;
    Double totalPrice;
}
