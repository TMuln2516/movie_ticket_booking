package com.example.booking_movie.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTicketRequest {
    @NotNull(message = "Name must not be null")
    String showtimeId;

    @NotEmpty
    Set<String> seatId;
}