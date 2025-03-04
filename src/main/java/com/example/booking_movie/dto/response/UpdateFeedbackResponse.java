package com.example.booking_movie.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateFeedbackResponse {
    String id;
    String date;
    String time;
    String content;
    Double rate;
    Boolean status;
    String movieId;
    String userId;
}
