package com.example.booking_movie.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.booking_movie.dto.response.GenreResponse;
import com.example.booking_movie.dto.response.MovieResponse;
import com.example.booking_movie.entity.Elastic.ElasticMovie;
import com.example.booking_movie.repository.GenreRepository;
import com.example.booking_movie.utils.DateUtils;
import com.example.booking_movie.utils.ElasticsearchUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ElasticsearchService {
    ElasticsearchClient elasticsearchClient;
    GenreRepository genreRepository;

//    @PostConstruct
//    public void checkAndCreateMovieIndex() {
//        String indexName = "movies";
//
//        try {
//            // Kiểm tra index đã tồn tại
//            boolean indexExists = elasticsearchClient.indices().exists(e -> e.index(indexName)).value();
//
//            if (!indexExists) {
//                // Tạo index với custom settings và mappings
//                String settingsAndMappings = """
//            {
//              "settings": {
//                "analysis": {
//                  "tokenizer": {
//                    "edge_ngram_tokenizer": {
//                      "type": "edge_ngram",
//                      "min_gram": 1,
//                      "max_gram": 25,
//                      "token_chars": ["letter", "digit"]
//                    }
//                  },
//                  "analyzer": {
//                    "custom_analyzer": {
//                      "type": "custom",
//                      "tokenizer": "edge_ngram_tokenizer",
//                      "filter": ["lowercase", "asciifolding"]
//                    }
//                  }
//                }
//              },
//              "mappings": {
//                "properties": {
//                  "name": {
//                    "type": "text",
//                    "analyzer": "custom_analyzer"
//                  }
//                }
//              }
//            }
//            """;
//
//                elasticsearchClient.indices().create(c -> c
//                        .index(indexName)
//                        .withJson(new java.io.StringReader(settingsAndMappings))
//                );
//
//                log.info("Custom index '{}' created successfully.", indexName);
//            } else {
//                log.info("Index '{}' already exists.", indexName);
//            }
//        } catch (IOException e) {
//            log.error("Error while creating index '{}': {}", indexName, e.getMessage(), e);
//        }
//    }

    public List<MovieResponse> fuzzyQuery(String value) throws IOException {
        // Tạo match query và fuzzy query
        Supplier<Query> matchQuerySupplier = ElasticsearchUtil.matchQuerySupplier(value);
        Supplier<Query> fuzzyQuerySupplier = ElasticsearchUtil.fuzzyQuerySupplier(value);

        // Thực hiện tìm kiếm với match query
        SearchResponse<ElasticMovie> searchResponse = elasticsearchClient.search(s -> s
                .index("movies")
                .query(matchQuerySupplier.get()), ElasticMovie.class);

        // Kiểm tra kết quả
        assert searchResponse.hits().total() != null;
        if (searchResponse.hits().total().value() == 0) {
            // Nếu không có kết quả từ match query, thực hiện fuzzy query
            log.info("No results from match query, performing fuzzy query...");
            searchResponse = elasticsearchClient.search(s -> s
                    .index("movies")
                    .query(fuzzyQuerySupplier.get()), ElasticMovie.class);
        }

        log.info("Elasticsearch query response: {}", searchResponse);

        // Chuyển đổi kết quả sang danh sách MovieResponse
        return searchResponse.hits().hits().stream().map(hit -> {
            ElasticMovie source = hit.source();
            assert source != null;
            return MovieResponse.builder()
                    .id(source.getId())
                    .name(source.getName())
                    .premiere(DateUtils.formatDate(DateUtils.epochToLocalDate(source.getPremiere())))
                    .language(source.getLanguage())
                    .duration(source.getDuration())
                    .content(source.getContent())
                    .rate(source.getRate())
                    .image(source.getImage())
                    .genres(source.getGenreIds().stream()
                            .map(genreId -> {
                                var genreInfo = genreRepository.findById(genreId).orElseThrow();
                                return GenreResponse.builder()
                                        .id(genreInfo.getId())
                                        .name(genreInfo.getName())
                                        .build();
                            })
                            .collect(Collectors.toList()))
                    .build();
        }).collect(Collectors.toList());
    }


    public Set<String> findSuggestedByMovieNames(String name) throws IOException {
        Query autoSuggestQuery = ElasticsearchUtil.buildAutoSuggestQuery(name);
        log.info("Elasticsearch query: {}", autoSuggestQuery.toString());

        SearchResponse<ElasticMovie> searchResponse = elasticsearchClient.search(q -> q.index("movies").query(autoSuggestQuery), ElasticMovie.class);

        log.info("Elasticsearch query response: {}", searchResponse);

        return searchResponse.hits().hits().stream()
                .map(Hit::source).filter(Objects::nonNull)
                .map(ElasticMovie::getName)
                .collect(Collectors.toSet());
    }
}

