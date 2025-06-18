package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Showtime;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, String> {
    List<Showtime> findAllByMovieId(String movieId);

    @Query("""
                SELECT s FROM Showtime s
                WHERE s.movie.id = :movieId
                  AND EXISTS (
                    SELECT 1 FROM Seat seat
                    WHERE seat.room = s.room AND seat.isCouple = true
                  )
            """)
    List<Showtime> findShowtimesWithCoupleSeatByMovieId(@Param("movieId") String movieId);

}
