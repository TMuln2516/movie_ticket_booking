package com.example.booking_movie.repository;

import com.example.booking_movie.entity.MatchingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchingRequestRepository extends JpaRepository<MatchingRequest, String> {
    @Query("SELECT m FROM MatchingRequest m WHERE " +
            "m.movieName = :movieName " +
            "AND m.showtime = :showtime " +
            "AND m.theaterName = :theaterName " +
            "AND m.isMatched = false " +
            "AND m.userId <> :userId")
    List<MatchingRequest> findMatchingRequests(
            @Param("movieName") String movieName,
            @Param("showtime") LocalDateTime showtime,
            @Param("theaterName") String theaterName,
            @Param("userId") String userId
    );

    @Query("SELECT COUNT(m)>0 FROM MatchingRequest m WHERE " +
            "m.movieName = :movieName " +
            "AND m.showtime = :showtime " +
            "AND m.theaterName = :theaterName " +
            "AND m.isMatched = false " +
            "AND m.userId = :userId")
    Boolean existMatchingRequests(
            @Param("movieName") String movieName,
            @Param("showtime") LocalDateTime showtime,
            @Param("theaterName") String theaterName,
            @Param("userId") String userId
    );
}
