package com.example.booking_movie.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateFeedbackRequest {
    @NotNull
    String content;

    @NotNull
    Double rate;

    @NotNull
    String movieId;
}
