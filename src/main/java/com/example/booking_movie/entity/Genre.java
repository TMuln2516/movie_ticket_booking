package com.example.booking_movie.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "genres")
    public class Genre {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        String id;
        String name;

        @ManyToMany(fetch = FetchType.LAZY, mappedBy = "genres")
        @JsonBackReference
        Set<Movie> movies;
    }
