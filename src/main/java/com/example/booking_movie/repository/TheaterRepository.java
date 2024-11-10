package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, String> {
    boolean existsByName(String name);

    List<Theater> findByLocation(String location);
}
