package com.example.booking_movie.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateSeatRequest {
    @NotNull(message = "Locate of row must not be null")
    Character locateRow;

    @NotNull(message = "Locate of column must not be null")
    Integer locateColumn;

    @NotNull(message = "Price must not be null")
    Double price;

    @NotNull(message = "Trạng thái của ghế không được để trống")
    Boolean isCouple;
}
