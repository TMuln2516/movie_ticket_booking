package com.example.booking_movie.dto.request;

import com.example.booking_movie.validator.EmailConstrain;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailRequest {
    @EmailConstrain
    String email;
}
