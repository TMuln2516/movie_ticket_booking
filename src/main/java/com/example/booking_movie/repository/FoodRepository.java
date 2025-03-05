package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Coupon;
import com.example.booking_movie.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, String> {
    boolean existsByName(String name);
}
