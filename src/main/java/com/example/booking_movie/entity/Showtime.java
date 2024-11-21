package com.example.booking_movie.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    LocalDate date;
    LocalTime startTime;
    LocalTime endTime;
    Integer totalSeat;
    Integer emptySeat;
    String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    @JsonBackReference
    Movie movie;

    @OneToMany(mappedBy = "showtime", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference
    Set<ScheduleSeat> scheduleSeats;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinTable(name = "showtime_room", joinColumns = {
            @JoinColumn(name = "showtime_id")}, inverseJoinColumns = {
            @JoinColumn(name = "room_id")})
    @JsonManagedReference
    Set<Room> rooms;

    @OneToMany(mappedBy = "showtime")
    @JsonManagedReference
    Set<Ticket> tickets;
}
