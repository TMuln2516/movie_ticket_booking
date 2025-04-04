package com.example.booking_movie.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "theaters")
public class Theater {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;
    String location;

    @OneToMany(mappedBy = "theater")
    @JsonManagedReference
    Set<Room> rooms;
}
