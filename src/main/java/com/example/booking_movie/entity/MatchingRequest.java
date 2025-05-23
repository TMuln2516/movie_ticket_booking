package com.example.booking_movie.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "matching_request")
public class MatchingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String userId;
    String movieName;
    String showtimeId;
    String theaterName;
    Boolean isMatched;
    Integer minAge;
    Integer maxAge;
    Boolean genderMatch;
    LocalDateTime createAt;
}
