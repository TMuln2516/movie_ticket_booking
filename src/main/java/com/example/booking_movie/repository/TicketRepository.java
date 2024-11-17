package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Genre;
import com.example.booking_movie.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {
    List<Ticket> findAllByFinished(Boolean status);
}
