package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    String endTime;
    String movieName;
    Double totalPrice;
}
