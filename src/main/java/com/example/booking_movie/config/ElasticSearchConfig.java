package com.example.booking_movie.config;

import lombok.NonNull;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticSearchConfig extends ElasticsearchConfiguration {
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .build();
    }

//    @Bean
//    public RestHighLevelClient client() {
//        RestClientBuilder builder = RestClient.builder("http://localhost:9200");
//        return new RestHighLevelClient(builder);
//    }
//
//    @Bean
//    public RestClient getRestClient() {
//        return RestClient.builder(new HttpHost("localhost", 9200)).build();
//    }
//
//    @Bean
//    public ElasticsearchTransport getElasticsearchTransport() {
//        return new RestClientTransport(getRestClient(), new JacksonJsonpMapper());
//    }
//
//    @Bean
//    public ElasticsearchClient getElasticsearchClient() {
//        return new ElasticsearchClient(getElasticsearchTransport());
//    }
}
