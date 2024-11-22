package com.example.booking_movie.initializer;

import com.example.booking_movie.entity.Theater;
import com.example.booking_movie.repository.TheaterRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TheaterInitializer {
    TheaterRepository theaterRepository;

    public void initializeTheaters() {
        if (!theaterRepository.existsByNameAndLocation("Galaxy Linh Trung Thủ Đức", "Hồ Chí Minh")) {
            theaterRepository.save(Theater.builder()
                            .name("Galaxy Linh Trung Thủ Đức")
                            .location("Hồ Chí Minh")
                    .build());
        }

        if (!theaterRepository.existsByNameAndLocation("CGV Vincom Thủ Đức", "Hồ Chí Minh")) {
            theaterRepository.save(Theater.builder()
                    .name("CGV Vincom Thủ Đức")
                    .location("Hồ Chí Minh")
                    .build());
        }

        if (!theaterRepository.existsByNameAndLocation("CGV Giga Mall Thủ Đức", "Hồ Chí Minh")) {
            theaterRepository.save(Theater.builder()
                    .name("CGV Giga Mall Thủ Đức")
                    .location("Hồ Chí Minh")
                    .build());
        }

        if (!theaterRepository.existsByNameAndLocation("CGV Vincom Đà Nẵng", "Đà Nẵng")) {
            theaterRepository.save(Theater.builder()
                    .name("CGV Vincom Đà Nẵng")
                    .location("Đà Nẵng")
                    .build());
        }

        if (!theaterRepository.existsByNameAndLocation("CGV Vincom Bà Triệu", "Hà Nội")) {
            theaterRepository.save(Theater.builder()
                    .name("CGV Vincom Bà Triệu")
                    .location("Hà Nội")
                    .build());
        }
    }
}
