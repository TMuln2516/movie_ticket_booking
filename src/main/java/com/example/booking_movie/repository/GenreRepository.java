package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, String> {
    boolean existsByName(String name);
    Optional<Genre> findByName(String name);
}
