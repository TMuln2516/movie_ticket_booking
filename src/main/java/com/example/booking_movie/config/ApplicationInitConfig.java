package com.example.booking_movie.config;

import com.example.booking_movie.initializer.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    RoleInitializer roleInitializer;
    UserInitializer userInitializer;
    JobInitializer jobInitializer;
    GenreInitializer genreInitializer;
    PersonInitializer personInitializer;
    MovieInitializer movieInitializer;
    TheaterInitializer theaterInitializer;
    RoomInitializer roomInitializer;

    @Bean
    ApplicationRunner applicationRunner() {
        log.info("Initializing application");
        return args -> {
            roleInitializer.initializeRoles();
            userInitializer.initializeUsers();
            jobInitializer.initializeJobs();
            genreInitializer.initializeGenres();
            personInitializer.initializePerson();
            movieInitializer.movieInitializer();

            theaterInitializer.initializeTheaters();
            roomInitializer.initializeRooms();
            log.info("Application initialization completed");
        };
    }
}
