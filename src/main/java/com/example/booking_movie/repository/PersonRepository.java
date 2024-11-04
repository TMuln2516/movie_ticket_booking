package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Person;
import jakarta.validation.constraints.Past;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, String> {
    boolean existsByNameAndGenderAndDateOfBirth(String name, Boolean gender, @Past(message = "Invalid Date") LocalDate dateOfBirth);
    List<Person> findByJobNameIgnoreCase(String jobName);
}
