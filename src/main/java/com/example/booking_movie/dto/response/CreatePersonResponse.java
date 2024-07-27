package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePersonResponse {
    String id;
    String name;
    Boolean gender;
    String dateOfBirth;
    String image;
}
