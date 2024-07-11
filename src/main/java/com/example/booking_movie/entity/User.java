package com.example.booking_movie.entity;

import com.example.booking_movie.validator.EmailConstrain;
import com.example.booking_movie.validator.PasswordConstrain;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 10, message = "Username must have at least 3 characters")
    String username;

    @PasswordConstrain
    String password;

    @NotNull(message = "First Name must not be null")
    String first_name;

    @NotNull(message = "Last Name must not be null")
    String last_name;

    Date date_of_birth;

    @NotNull(message = "Invalid gender")
    Boolean gender;

    @EmailConstrain
    String email;

    String avatar;

    @NotNull(message = "Status must not be null")
    Boolean status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", joinColumns = {
            @JoinColumn(name = "user_id")}, inverseJoinColumns = {
            @JoinColumn(name = "role_id")})
    @JsonManagedReference
    Set<Role> roles;
}