package com.example.booking_movie.initializer;

import com.example.booking_movie.constant.DefinedJob;
import com.example.booking_movie.entity.Genre;
import com.example.booking_movie.entity.Job;
import com.example.booking_movie.repository.GenreRepository;
import com.example.booking_movie.repository.JobRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreInitializer {
    GenreRepository genreRepository;

    public void initializeGenres() {
        if (!genreRepository.existsByName("Lãng Mạn")) {
            genreRepository.save(Genre.builder()
                            .name("Lãng Mạn")
                    .build());
        }

        if (!genreRepository.existsByName("Chính Kịch")) {
            genreRepository.save(Genre.builder()
                    .name("Chính Kịch")
                    .build());
        }

        if (!genreRepository.existsByName("Hư Cấu Lịch Sử")) {
            genreRepository.save(Genre.builder()
                    .name("Hư Cấu Lịch Sử")
                    .build());
        }


        if (!genreRepository.existsByName("Chiến Tranh")) {
            genreRepository.save(Genre.builder()
                    .name("Chiến Tranh")
                    .build());
        }

        if (!genreRepository.existsByName("Kinh Dị")) {
            genreRepository.save(Genre.builder()
                    .name("Kinh Dị")
                    .build());
        }
    }
}
