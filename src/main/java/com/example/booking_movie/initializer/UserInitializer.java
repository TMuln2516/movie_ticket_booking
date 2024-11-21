package com.example.booking_movie.initializer;

import com.example.booking_movie.constant.DefinedRole;
import com.example.booking_movie.entity.Role;
import com.example.booking_movie.entity.User;
import com.example.booking_movie.repository.RoleRepository;
import com.example.booking_movie.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserInitializer {
    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "Amin123@";
    private static final String ADMIN_FIRSTNAME = "admin";
    private static final String ADMIN_LASTNAME = "admin";
    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final Boolean ADMIN_GENDER = true;
    private static final Boolean ADMIN_STATUS = true;

    private static final String MANAGER_USERNAME = "manager";
    private static final String MANAGER_PASSWORD = "Manager123@";
    private static final String MANAGER_FIRSTNAME = "manager";
    private static final String MANAGER_LASTNAME = "manager";
    private static final String MANAGER_EMAIL = "manager@gmail.com";
    private static final Boolean MANAGER_GENDER = true;
    private static final Boolean MANAGER_STATUS = true;

    public void initializeUsers() {
        if (!userRepository.existsByUsername(ADMIN_USERNAME)) {
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findById(DefinedRole.ADMIN_ROLE).orElseThrow());
            userRepository.save(User.builder()
                    .username(ADMIN_USERNAME)
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .firstName(ADMIN_FIRSTNAME)
                    .lastName(ADMIN_LASTNAME)
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
                    .firstName(MANAGER_FIRSTNAME)
                    .lastName(MANAGER_LASTNAME)
                    .email(MANAGER_EMAIL)
                    .gender(MANAGER_GENDER)
                    .status(MANAGER_STATUS)
                    .roles(roles)
                    .build());
        }
    }
}
