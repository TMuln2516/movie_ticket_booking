package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.CreateRoleRequest;
import com.example.booking_movie.entity.Role;
import com.example.booking_movie.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;

    public void createRole(CreateRoleRequest createRoleRequest) {
        roleRepository.save(Role.builder()
                .name(createRoleRequest.getName())
                .description(createRoleRequest.getDescription())
                .build());
    }

    public boolean existedByName(String roleName) {
        return roleRepository.existsByName(roleName);
    }
}
