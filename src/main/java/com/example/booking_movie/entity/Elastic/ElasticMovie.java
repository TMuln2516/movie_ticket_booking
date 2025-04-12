package com.example.booking_movie.entity.Elastic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(indexName = "movies")
//@Setting(settingPath = "static/MovieSettings.json")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticMovie {
    @Id
    String id;

//    @Field(type = FieldType.Text, analyzer = "vietnamese_analyzer", searchAnalyzer = "vietnamese_search_analyzer")
    String name;

    Long premiere;
    String language;
    Integer duration;
    String content;
    Double rate;
    String image;
    String publicId;
    Long createAt;

    Set<String> genreIds;
    Set<String> personIds;
    Set<String> showtimeIds;
}
