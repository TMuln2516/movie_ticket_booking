package com.example.booking_movie.dto.response;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotifyResponse {
    String id;
    Integer code;
    LocalDateTime createdAt;
    Object data;
    String message;
}
