package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponResponse {
    String id;
    String code;
    String discountType;
    Integer discountValue;
    LocalDate startDate;
    LocalDate endDate;
    Double minValue;
    String description;
    String image;
    String publicId;
    Boolean status;
}
