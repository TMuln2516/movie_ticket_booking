package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ActorRepository extends JpaRepository<Actor, String> {
    boolean existsByNameAndGenderAndDateOfBirthAndJob(String name, Boolean gender, Date date_of_birth, String job);
}
