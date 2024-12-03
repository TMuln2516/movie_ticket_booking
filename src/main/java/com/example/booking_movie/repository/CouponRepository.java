package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Coupon;
import com.example.booking_movie.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {
    boolean existsByCode(String code);

    Optional<Coupon> findByCode(String code);

    @Query("SELECT c FROM Coupon c WHERE c NOT IN " +
            "(SELECT t.coupon FROM Ticket t WHERE t.user.id = :userId AND t.coupon IS NOT NULL AND t.status = true)")
    List<Coupon> findUnusedCouponsByUser(@Param("userId") String userId);

}
