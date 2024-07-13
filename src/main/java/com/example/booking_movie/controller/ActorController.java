package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.CreateActorRequest;
import com.example.booking_movie.dto.request.UpdateActorRequest;
import com.example.booking_movie.dto.response.ActorResponse;
import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.dto.response.CreateActorResponse;
import com.example.booking_movie.dto.response.UpdateActorResponse;
import com.example.booking_movie.service.ActorService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActorController {
    ActorService actorService;

//    get all
    @GetMapping("/")
    public ApiResponse<List<ActorResponse>> getAll() {
        return ApiResponse.<List<ActorResponse>>builder()
                .message("Get All Actor Success")
                .result(actorService.getAll())
                .build();
    }

//    create actor
    @PostMapping("/")
    public ApiResponse<CreateActorResponse> create(@RequestBody @Valid CreateActorRequest createActorRequest) {
        return ApiResponse.<CreateActorResponse>builder()
                .message("Create Actor Success")
                .result(actorService.create(createActorRequest))
                .build();
    }

//    update actor
    @PutMapping("/{id}")
    public ApiResponse<UpdateActorResponse> update(@PathVariable String id, @RequestBody @Valid UpdateActorRequest updateActorRequest) {
        return ApiResponse.<UpdateActorResponse>builder()
                .message("Update Actor Success")
                .result(actorService.update(id, updateActorRequest))
                .build();
    }

//    delete actor
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        actorService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Delete Actor Success")
                .build();
    }
}
