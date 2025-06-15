package com.example.booking_movie.repository;

import com.example.booking_movie.entity.TicketDetails;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TicketDetailsRepository extends JpaRepository<TicketDetails, String> {
    List<TicketDetails> findAllByTicketId(String ticketId);

    @Query("SELECT td FROM TicketDetails td " +
            "WHERE td.ticket.status = true AND td.ticket.date BETWEEN :start AND :end")
    List<TicketDetails> findTicketDetailsBetweenDates(@Param("start") LocalDate start, @Param("end") LocalDate end);

}
