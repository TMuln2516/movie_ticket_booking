//package com.example.booking_movie.entity.Elastic;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import lombok.*;
//import lombok.experimental.FieldDefaults;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.Document;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.Set;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//@FieldDefaults(level = AccessLevel.PRIVATE)
//@Document(indexName = "tickets")
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class ElasticTicket {
//    @Id
//    String id;
//
//    LocalDateTime date;
//    String showtimeId;
//    String movieId;
//    String theaterId;
//    Double amount;
//}
