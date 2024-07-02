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

import java.util.HashSet;
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
    static final String ADMIN_PASSWORD = "admin";

    @NonFinal
    static final String MANAGER_USERNAME = "manager";

    @NonFinal
    static final String MANAGER_PASSWORD = "manager";

    @Bean
    ApplicationRunner applicationRunner(UserService userService, RoleService roleService) {
        log.info("Initializing application");
        return args -> {
            if (!roleService.existedByName(DefinedRole.ADMIN_ROLE)) {
                roleService.createRole(CreateRoleRequest.builder()
                        .name(DefinedRole.ADMIN_ROLE)
                        .description("Admin Role")
                        .build());
            }

            if (!roleService.existedByName(DefinedRole.MANAGER_ROLE)) {
                roleService.createRole(CreateRoleRequest.builder()
                        .name(DefinedRole.MANAGER_ROLE)
                        .description("Manager Role")
                        .build());
            }

            if (!roleService.existedByName(DefinedRole.USER_ROLE)) {
                roleService.createRole(CreateRoleRequest.builder()
                        .name(DefinedRole.USER_ROLE)
                        .description("User Role")
                        .build());
            }

            if (!userService.existedByUsername(ADMIN_USERNAME)) {
                userService.createUser(CreateUserRequest.builder()
                        .username(ADMIN_USERNAME)
                        .password(ADMIN_PASSWORD)
                        .gender(true)
                        .build());
            }

            if (!userService.existedByUsername(MANAGER_USERNAME)) {
                userService.createUser(CreateUserRequest.builder()
                        .username(MANAGER_USERNAME)
                        .password(MANAGER_PASSWORD)
                        .gender(true)
                        .build());
            }
            log.info("Application initialization completed");
        };
    }
}
