package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Boolean existsByName(String name);
}
