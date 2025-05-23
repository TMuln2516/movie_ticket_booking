package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetTicketDetailResponse {
    String id;
    Double price;
    String ticketId;
    SeatResponse seat;
}
