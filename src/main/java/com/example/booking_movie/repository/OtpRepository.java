package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Genre;
import com.example.booking_movie.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, String> {
    boolean existsByEmail(String email);
    Optional<Otp> findByEmail(String email);
}
