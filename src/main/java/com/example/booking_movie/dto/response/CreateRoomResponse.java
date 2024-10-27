package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRoomResponse {
    String id;
    String name;
    Integer columns;
    Integer rows;
}
