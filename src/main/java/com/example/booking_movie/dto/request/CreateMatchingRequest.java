package com.example.booking_movie.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMatchingRequest {
    @NotNull(message = "Tên phim không được để trống")
    String movieName;

    @NotNull(message = "Suất chiếu không được để trống")
    String showtimeId;

    @NotNull(message = "Tên rạp chiếu phim không được để trống")
    String theaterName;

    @NotNull(message = "Giới tính không được để trống")
    Boolean gender;

    @NotNull(message = "Độ tuổi tối thiểu không được để trống")
    @Min(value = 18, message = "Độ tuổi tối thiểu phải từ 18 trở lên")
    Integer minAge;

    @NotNull(message = "Độ tuổi tối đa không được để trống")
    Integer maxAge;
}
