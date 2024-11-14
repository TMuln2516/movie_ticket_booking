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
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;

    @OneToMany(mappedBy = "job")
    @JsonManagedReference
    Set<Person> persons;
}
