package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String> {
    boolean existsByName(String name);
    List<Movie> findAllByGenresId(String genreId);
}
