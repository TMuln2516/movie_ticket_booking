package com.example.booking_movie.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRoleRequest {
    @NotNull(message = "Name must not be null")
    String name;

    @NotNull(message = "Description must not be null")
    String description;
}
