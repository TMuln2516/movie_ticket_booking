package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Genre, String> {
}
