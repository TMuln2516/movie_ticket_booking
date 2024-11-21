package com.example.booking_movie.repository;

import com.example.booking_movie.entity.InvalidatedToken;
import com.example.booking_movie.entity.ScheduleSeat;
import com.example.booking_movie.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ScheduleSeatRepository extends JpaRepository<ScheduleSeat, String> {
    List<ScheduleSeat> findAllByShowtimeId(String showtimeId);
    ScheduleSeat findByShowtimeIdAndSeatId(String showtimeId, String seatId);
}
