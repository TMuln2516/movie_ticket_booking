package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, String> {
}
