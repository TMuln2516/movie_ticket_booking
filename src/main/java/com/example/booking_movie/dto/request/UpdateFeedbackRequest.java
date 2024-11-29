package com.example.booking_movie.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateFeedbackRequest {
    @NotNull
    String content;

    @NotNull
    Double rate;

    @NotNull
    String date;

    @NotNull
    String time;
}
