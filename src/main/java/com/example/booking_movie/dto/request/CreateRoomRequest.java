package com.example.booking_movie.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRoomRequest {
    @NotNull(message = "Name must not be null")
    String name;

    @NotNull(message = "Number of row must not be null")
    Integer rows;

    @NotNull(message = "Number of column must not be null")
    Integer columns;

    Set<String> coupleRows;
}
