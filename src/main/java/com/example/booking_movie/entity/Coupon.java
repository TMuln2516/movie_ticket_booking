package com.example.booking_movie.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@Table(name = "coupons")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String code;
    String discountType;
    Integer discountValue;
    LocalDate startDate;
    LocalDate endDate;
    String description;
    Double minValue;
    Boolean status;
    String image;
    String publicId;

    @OneToMany(mappedBy = "coupon")
    @JsonManagedReference
    Set<Ticket> tickets;
}
