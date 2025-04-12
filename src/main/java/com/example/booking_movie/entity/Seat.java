package com.example.booking_movie.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "seats")
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Seat.class)
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    Character locateRow;
    Integer locateColumn;
    Double price;
    Boolean isCouple;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @JsonBackReference
    Room room;

    @OneToMany(mappedBy = "seat")
    @JsonManagedReference
    Set<ScheduleSeat> scheduleSeats;

    @OneToMany(mappedBy = "seat")
    @JsonManagedReference
    Set<TicketDetails> ticketDetails;
}
