package com.example.booking_movie.dto.request;

import com.example.booking_movie.validator.PasswordConstrain;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateFoodRequest {
    @NotNull(message = "Tên của món ăn không được để trống")
    String name;

    @Positive(message = "Giá của món ăn phải là số dương")
    Double price;
}
