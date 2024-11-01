package com.example.booking_movie.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateBioRequest {
    String firstName;
    String lastName;
    Date dateOfBirth;
    String email;
    String avatar;
}
