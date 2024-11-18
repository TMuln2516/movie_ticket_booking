package com.example.booking_movie.entity.Elastic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
//@Document(indexName = "movies")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticMovie {
    @Id
    String id;

    String name;
    Long premiere;
    String language;
    Integer duration;
    String content;
    Double rate;
    String image;
    String publicId;

    Set<String> genreIds;
    Set<String> personIds;
    Set<String> showtimeIds;
}
