package com.example.booking_movie.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "showtimes")
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    Date date;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Integer totalSeat;
    Integer emptySeat;
    Boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    @JsonBackReference
    Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id")
    @JsonBackReference
    Theater theater;

    @OneToMany(mappedBy = "showtime")
    @JsonManagedReference
    Set<ScheduleSeat> scheduleSeats;
}
