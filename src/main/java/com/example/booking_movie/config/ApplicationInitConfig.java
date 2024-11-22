package com.example.booking_movie.config;

import com.example.booking_movie.constant.DefinedJob;
import com.example.booking_movie.constant.DefinedRole;
import com.example.booking_movie.entity.Job;
import com.example.booking_movie.entity.Role;
import com.example.booking_movie.entity.User;
import com.example.booking_movie.initializer.*;
import com.example.booking_movie.repository.JobRepository;
import com.example.booking_movie.repository.RoleRepository;
import com.example.booking_movie.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

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
