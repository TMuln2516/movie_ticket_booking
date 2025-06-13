package com.example.booking_movie.repository;

import com.example.booking_movie.entity.ScheduleSeat;
import com.example.booking_movie.entity.Seat;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleSeatRepository extends JpaRepository<ScheduleSeat, String> {
    List<ScheduleSeat> findAllByShowtimeId(String showtimeId);

    ScheduleSeat findByShowtimeIdAndSeatId(String showtimeId, String seatId);

    @Query("SELECT s.seat FROM ScheduleSeat s " +
            "WHERE s.showtime.id = :showtimeId " +
            "AND s.seat.isCouple = true " +
            "AND (s.status = false OR s.status IS NULL)")
    List<Seat> findAvailableCoupleSeats(@Param("showtimeId") String showtimeId);
}
