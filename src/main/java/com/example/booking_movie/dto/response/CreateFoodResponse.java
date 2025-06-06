package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateFoodResponse {
    String id;
    String name;
    Double price;
    String image;
    String publicId;
}
