package com.example.booking_movie.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
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
    LocalDate premiere;
    String language;
    Integer duration;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String content;
    Double rate;
    String image;
    String publicId;

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

    @OneToMany(mappedBy = "movie")
    @JsonManagedReference
    Set<Showtime> showtimes;

    @OneToMany(mappedBy = "movie")
    @JsonManagedReference
    Set<Feedback> feedbacks;
}
