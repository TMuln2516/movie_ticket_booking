package com.example.booking_movie.initializer;

import com.example.booking_movie.constant.DefinedRole;
import com.example.booking_movie.entity.Role;
import com.example.booking_movie.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleInitializer {
    RoleRepository roleRepository;

    public void initializeRoles() {
        if (!roleRepository.existsByName(DefinedRole.ADMIN_ROLE)) {
            roleRepository.save(Role.builder()
                    .name(DefinedRole.ADMIN_ROLE)
                    .description("Người quản lý hệ thống")
                    .build());
        }

//        if (!roleRepository.existsByName(DefinedRole.MANAGER_ROLE)) {
//            roleRepository.save(Role.builder()
//                    .name(DefinedRole.MANAGER_ROLE)
//                    .description("Manager Role")
//                    .build());
//        }

//        if (!roleRepository.existsByName(DefinedRole.THEATER_PARTNER_ROLE)) {
//            roleRepository.save(Role.builder()
//                    .name(DefinedRole.THEATER_PARTNER_ROLE)
//                    .description("Rạp đối tác của hệ thống")
//                    .build());
//        }

        if (!roleRepository.existsByName(DefinedRole.USER_ROLE)) {
            roleRepository.save(Role.builder()
                    .name(DefinedRole.USER_ROLE)
                    .description("Người dùng hệ thống")
                    .build());
        }
    }
}
