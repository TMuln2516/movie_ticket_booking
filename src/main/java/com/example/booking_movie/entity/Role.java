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
@Table(name = "roles")
public class Role {
    @Id
    String name;

    @NotNull(message = "Description must not be null")
    String description;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    @JsonBackReference
    Set<User> users;

}
