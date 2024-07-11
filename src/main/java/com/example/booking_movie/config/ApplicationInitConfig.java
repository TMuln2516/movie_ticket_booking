package com.example.booking_movie.config;

import com.example.booking_movie.constant.DefinedRole;
import com.example.booking_movie.dto.request.CreateRoleRequest;
import com.example.booking_movie.dto.request.CreateUserRequest;
import com.example.booking_movie.entity.Role;
import com.example.booking_movie.entity.User;
import com.example.booking_movie.repository.RoleRepository;
import com.example.booking_movie.repository.UserRepository;
import com.example.booking_movie.service.RoleService;
import com.example.booking_movie.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_USERNAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "Amin123@";

    @NonFinal
    static final String ADMIN_FIRSTNAME = "admin";

    @NonFinal
    static final String ADMIN_LASTNAME = "admin";

    @NonFinal
    static final String ADMIN_EMAIL = "admin@gmail.com";

    @NonFinal
    static final Boolean ADMIN_GENDER = true;

    @NonFinal
    static final Boolean ADMIN_STATUS = true;

    @NonFinal
    static final String MANAGER_USERNAME = "manager";

    @NonFinal
    static final String MANAGER_PASSWORD = "Manager123@";

    @NonFinal
    static final String MANAGER_FIRSTNAME = "manager";

    @NonFinal
    static final String MANAGER_LASTNAME = "manager";

    @NonFinal
    static final String MANAGER_EMAIL = "manager@gmail.com";

    @NonFinal
    static final Boolean MANAGER_GENDER = true;

    @NonFinal
    static final Boolean MANAGER_STATUS = true;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        log.info("Initializing application");
        return args -> {
            if (!roleRepository.existsByName(DefinedRole.ADMIN_ROLE)) {
                roleRepository.save(Role.builder()
                        .name(DefinedRole.ADMIN_ROLE)
                        .description("Admin Role")
                        .build());
            }

            if (!roleRepository.existsByName(DefinedRole.MANAGER_ROLE)) {
                roleRepository.save(Role.builder()
                        .name(DefinedRole.MANAGER_ROLE)
                        .description("Manager Role")
                        .build());
            }
            if (!roleRepository.existsByName(DefinedRole.USER_ROLE)) {
                roleRepository.save(Role.builder()
                        .name(DefinedRole.USER_ROLE)
                        .description("User Role")
                        .build());
            }

            if (!userRepository.existsByUsername(ADMIN_USERNAME)) {
                Set<Role> roles = new HashSet<>();
                roles.add(roleRepository.findById(DefinedRole.ADMIN_ROLE).orElseThrow());
                userRepository.save(User.builder()
                        .username(ADMIN_USERNAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .first_name(ADMIN_FIRSTNAME)
                        .last_name(ADMIN_LASTNAME)
                        .email(ADMIN_EMAIL)
                        .gender(ADMIN_GENDER)
                        .status(ADMIN_STATUS)
                        .roles(roles)
                        .build());
            }

            if (!userRepository.existsByUsername(MANAGER_USERNAME)) {
                Set<Role> roles = new HashSet<>();
                roles.add(roleRepository.findById(DefinedRole.MANAGER_ROLE).orElseThrow());
                userRepository.save(User.builder()
                        .username(MANAGER_USERNAME)
                        .password(passwordEncoder.encode(MANAGER_PASSWORD))
                        .first_name(MANAGER_FIRSTNAME)
                        .last_name(MANAGER_LASTNAME)
                        .email(MANAGER_EMAIL)
                        .gender(MANAGER_GENDER)
                        .status(MANAGER_STATUS)
                        .roles(roles)
                        .build());
            }
            log.info("Application initialization completed");
        };
    }
}
