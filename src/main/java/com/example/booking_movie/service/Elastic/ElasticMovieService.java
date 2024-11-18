package com.example.booking_movie.service.Elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.example.booking_movie.entity.Elastic.ElasticMovie;
import com.example.booking_movie.repository.Elastic.ElasticMovieRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ElasticMovieService {
    ElasticsearchClient elasticsearchClient;

    public void createOrUpdate(ElasticMovie movie) {
        try {
            String indexName = "movies";

            IndexResponse response = elasticsearchClient.index(i -> i
                    .index(indexName)
                    .id(movie.getId())
                    .document(movie)
            );

            log.info("Document indexed with ID: {}", response.id());
        } catch (IOException e) {
            log.error("Error while indexing document: {}", e.getMessage(), e);
        }
    }
}
