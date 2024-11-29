package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, String> {
    void deleteAllByUserId(String userId);

    Optional<Feedback> findByMovieIdAndUserIdAndDateAndTime(String movieId, String userId, LocalDate date, LocalTime time);
}
