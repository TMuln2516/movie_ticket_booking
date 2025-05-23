package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetOneShowtimeResponses {
    String id;
    String date;
    String startTime;
    String endTime;
    Integer totalSeat;
    Integer emptySeat;
    String status;
    TheaterResponse theater;
    MovieDetailResponse movie;
    RoomResponse room;
}
