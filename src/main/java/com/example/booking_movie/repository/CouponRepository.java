package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Coupon;
import com.example.booking_movie.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {
    boolean existsByCode(String code);
}
