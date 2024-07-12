package com.example.booking_movie.dto.response;

import com.example.booking_movie.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    String firstName;
    String lastName;
    String dateOfBirth;
    Boolean gender;
    String email;
    String avatar;
    Set<Role> roles;
}
