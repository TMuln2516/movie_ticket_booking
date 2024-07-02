package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BioResponse {
    String id;
    String username;
    String password;
    String first_name;
    String last_name;
    String date_of_birth;
    Boolean gender;
    String email;
    String avatar;
}
