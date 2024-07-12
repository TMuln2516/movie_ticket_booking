package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserResponse {
    String id;
    String username;
    String password;
    String firstName;
    String lastName;
    String dateOfBirth;
    Boolean gender;
    String email;
    String avatar;
}
