package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Genre;
import com.example.booking_movie.entity.TicketDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketDetailsRepository extends JpaRepository<TicketDetails, String> {
}
