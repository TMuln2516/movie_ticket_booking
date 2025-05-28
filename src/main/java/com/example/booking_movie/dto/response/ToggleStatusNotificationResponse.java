package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ToggleStatusNotificationResponse {
    String id;
    String data;
    Boolean isRead;
    String message;
    UserResponse user;
}
