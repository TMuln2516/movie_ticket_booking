package com.example.booking_movie.config;

import com.example.booking_movie.entity.Elastic.ElasticMovie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    protected String elasticUri;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticUri)
                .build();
    }

    // Không cần tự tạo ElasticsearchOperations, Spring sẽ tự inject
//    @Autowired
//    public void createIndex(ElasticsearchOperations elasticsearchOperations) {
//        IndexOperations indexOps = elasticsearchOperations.indexOps(IndexCoordinates.of("movies"));
//
//        // Kiểm tra nếu index chưa tồn tại thì tạo mới
//        if (!indexOps.exists()) {
//            Map<String, Object> settings = new HashMap<>();
//            settings.put("analysis", Map.of(
//                    "filter", Map.of(
//                            "vietnamese_ngram", Map.of(
//                                    "type", "ngram",
//                                    "min_gram", 1,
//                                    "max_gram", 10
//                            ),
//                            "vietnamese_folding", Map.of(
//                                    "type", "asciifolding",
//                                    "preserve_original", true
//                            )
//                    ),
//                    "analyzer", Map.of(
//                            "vietnamese_analyzer", Map.of(
//                                    "type", "custom",
//                                    "tokenizer", "standard",
//                                    "filter", List.of("lowercase", "vietnamese_ngram", "vietnamese_folding")
//                            ),
//                            "vietnamese_search_analyzer", Map.of(
//                                    "type", "custom",
//                                    "tokenizer", "standard",
//                                    "filter", List.of("lowercase", "vietnamese_folding")
//                            )
//                    )
//            ));
//
//            indexOps.create(settings);
//            indexOps.putMapping(indexOps.createMapping(ElasticMovie.class));
//            System.out.println("Index 'movies' created with custom analyzers.");
//        }
//    }
}