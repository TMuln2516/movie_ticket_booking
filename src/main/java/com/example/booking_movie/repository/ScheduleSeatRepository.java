package com.example.booking_movie.repository;

import com.example.booking_movie.entity.ScheduleSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleSeatRepository extends JpaRepository<ScheduleSeat, String> {
    List<ScheduleSeat> findAllByShowtimeId(String showtimeId);
    ScheduleSeat findByShowtimeIdAndSeatId(String showtimeId, String seatId);
}
