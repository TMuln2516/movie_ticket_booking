package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, String> {
    boolean existsByName(String name);
}
