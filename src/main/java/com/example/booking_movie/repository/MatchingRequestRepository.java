package com.example.booking_movie.repository;

import com.example.booking_movie.entity.MatchingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchingRequestRepository extends JpaRepository<MatchingRequest, String> {
    @Query("SELECT m FROM MatchingRequest m WHERE " +
            "m.movieName = :movieName " +
            "AND m.showtimeId = :showtimeId " +
            "AND m.theaterName = :theaterName " +
            "AND m.isMatched = false " +
            "AND (:minAge <= m.maxAge AND :maxAge >= m.minAge) " +
            "AND m.genderMatch = :currentUserGender " +
            "AND m.userId <> :userId")
    List<MatchingRequest> findMatchingRequests(
            @Param("movieName") String movieName,
            @Param("showtimeId") String showtimeId,
            @Param("theaterName") String theaterName,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("currentUserGender") Boolean currentUserGender,
            @Param("userId") String userId
    );


    @Query("SELECT COUNT(m)>0 FROM MatchingRequest m WHERE " +
            "m.movieName = :movieName " +
            "AND m.showtimeId = :showtimeId " +
            "AND m.theaterName = :theaterName " +
            "AND m.isMatched = false " +
            "AND m.minAge >= :minAge " +
            "AND m.maxAge <= :maxAge " +
            "AND m.genderMatch = :genderMatch " +
            "AND m.userId = :userId")
    Boolean existMatchingRequests(
            @Param("movieName") String movieName,
            @Param("showtimeId") String showtimeId,
            @Param("theaterName") String theaterName,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("genderMatch") Boolean genderMatch,
            @Param("userId") String userId
    );

    Optional<MatchingRequest> findTopByUserIdOrderByCreateAtDesc(String userId);

    List<MatchingRequest> findAllByUserId(String userId);

    @Query("SELECT m FROM MatchingRequest m WHERE m.isMatched = false")
    List<MatchingRequest> findUnmatchedRequests();
}
