package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetAllTicketResponse {
    String id;
    String date;
    String time;
    Boolean status;
    Double amount;
    UserResponse user;
    GetAllShowtimeResponses showtime;
    List<FoodDetailResponse> foods;
    CouponResponse coupon;
}
