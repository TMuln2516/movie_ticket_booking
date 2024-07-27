package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, String> {
    boolean existsByName(String name);
    Optional<Job> findByName(String name);
}
