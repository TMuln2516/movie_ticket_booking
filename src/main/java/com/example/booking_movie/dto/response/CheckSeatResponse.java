package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckSeatResponse {
    String id;
    Character locateRow;
    Integer locateColumn;
    Double price;
    Boolean isCouple;
    Integer isBooked;
}
