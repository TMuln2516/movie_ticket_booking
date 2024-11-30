package com.example.booking_movie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "otps")
    public class Otp {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        String id;

        String otp;
        LocalDateTime expiryTime;
        String email;
    }
