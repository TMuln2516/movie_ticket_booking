package com.example.booking_movie.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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
    String name;
    Date premiere;
    String language;
    Integer duration;
    String content;
    Double rate;
    String image;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "movie_genre", joinColumns = {
            @JoinColumn(name = "movie_id")}, inverseJoinColumns = {
            @JoinColumn(name = "genre_id")})
    @JsonManagedReference
    Set<Genre> genres;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "movie_person", joinColumns = {
            @JoinColumn(name = "movie_id")}, inverseJoinColumns = {
            @JoinColumn(name = "person_id")})
    @JsonManagedReference
    Set<Person> persons;
}
