package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.CreateActorRequest;
import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.dto.response.CreateActorResponse;
import com.example.booking_movie.service.ActorService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/actors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActorController {
    ActorService actorService;

//    create actor
    @PostMapping("/")
    public ApiResponse<CreateActorResponse> create(@RequestBody @Valid CreateActorRequest createActorRequest) {
        return ApiResponse.<CreateActorResponse>builder()
                .message("Create Actor Success")
                .result(actorService.create(createActorRequest))
                .build();
    }
}
