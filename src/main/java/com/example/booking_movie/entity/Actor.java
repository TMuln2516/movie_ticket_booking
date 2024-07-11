package com.example.booking_movie.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "actors")
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @NotNull(message = "Name must not be null")
    String name;

    @NotNull(message = "Invalid gender")
    Boolean gender;

    @Past(message = "Invalid Date")
    Date date_of_birth;

    @NotNull(message = "Description must not be null")
    String description;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "actors")
    @JsonBackReference
    Set<Movie> movies;
}
