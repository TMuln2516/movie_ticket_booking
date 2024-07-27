package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, String> {
    boolean existsByNameAndGenderAndDateOfBirth(String name, Boolean gender, Date dateOfBirth);
    List<Person> findByJobNameIgnoreCase(String jobName);
}
