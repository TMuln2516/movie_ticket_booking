package com.example.booking_movie.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @NotNull(message = "Name must not be null")
    String name;

    @NotNull(message = "Premiere Day must not be null")
    @Future(message = "Invalid date")
    Date premiere;

    @NotNull(message = "Language must not be null")
    String language;

    @Size(min = 60, message = "Duration must be at least 60 minutes")
    Integer duration;

    @NotNull(message = "Content must not be null")
    String content;

    Double rate;

    @NotNull(message = "Image must not be null")
    String image;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "movie_genre", joinColumns = {
            @JoinColumn(name = "movie_id")}, inverseJoinColumns = {
            @JoinColumn(name = "genre_id")})
    @JsonManagedReference
    Set<Genre> genres;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "movie_actor", joinColumns = {
            @JoinColumn(name = "movie_id")}, inverseJoinColumns = {
            @JoinColumn(name = "actor_id")})
    @JsonManagedReference
    Set<Actor> actors;
}
