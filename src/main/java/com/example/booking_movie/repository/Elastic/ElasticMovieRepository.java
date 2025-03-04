package com.example.booking_movie.repository.Elastic;

import com.example.booking_movie.entity.Elastic.ElasticMovie;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ElasticMovieRepository extends ElasticsearchRepository<ElasticMovie, String> {
}
