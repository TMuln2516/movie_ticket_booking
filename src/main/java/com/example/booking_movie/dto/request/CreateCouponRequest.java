package com.example.booking_movie.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCouponRequest {
    @NotNull
    String code;

    @NotNull
    String discountType;

    @NotNull
    @PositiveOrZero(message = "Giảm giá phải là số dương")
    Integer discountValue;

    @Future(message = "Ngày không hợp lệ")
    LocalDate startDate;

    @Future(message = "Ngày không hợp lệ")
    LocalDate endDate;

    @NotNull
    String description;

    @NotNull
    @Positive(message = "Giá trị đơn hàng tối thiểu phải là số dương")
    Double minValue;
}
