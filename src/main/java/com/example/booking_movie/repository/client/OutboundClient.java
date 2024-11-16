package com.example.booking_movie.repository.client;

import com.example.booking_movie.dto.request.ExchangeTokenRequest;
import com.example.booking_movie.dto.response.ExchangeTokenResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "outbound-client", url = "https://oauth2.googleapis.com")
public interface OutboundClient {
    @PostMapping(value = "/token", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ExchangeTokenResponse exchangeToken(@QueryMap ExchangeTokenRequest exchangeTokenRequest);
}
