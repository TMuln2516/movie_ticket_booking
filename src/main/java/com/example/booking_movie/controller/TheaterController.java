package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.CreateTheaterRequest;
import com.example.booking_movie.dto.request.UpdateTheaterRequest;
import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.dto.response.CreateTheaterResponse;
import com.example.booking_movie.dto.response.TheaterResponse;
import com.example.booking_movie.dto.response.UpdateTheaterResponse;
import com.example.booking_movie.service.TheaterService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TheaterController {
    TheaterService theaterService;

//    ROLE MANAGER AND USER
//    get all theater
@GetMapping("/")
public ApiResponse<List<TheaterResponse>> getAll() {
    return ApiResponse.<List<TheaterResponse>>builder()
            .message("Get All Theater Success")
            .result(theaterService.getAll())
            .build();
}

//    ROLE MANAGER
//    create theater
    @PostMapping("/")
    public ApiResponse<CreateTheaterResponse> create(@RequestBody @Valid CreateTheaterRequest createTheaterRequest) {
        return ApiResponse.<CreateTheaterResponse>builder()
                .message("Create Theater Success")
                .result(theaterService.create(createTheaterRequest))
                .build();
    }

//    update theater
    @PutMapping("/{theaterId}")
    public ApiResponse<UpdateTheaterResponse> update(@PathVariable String theaterId, @RequestBody UpdateTheaterRequest updateTheaterRequest) {
        return ApiResponse.<UpdateTheaterResponse>builder()
                .message("Update Theater Success")
                .result(theaterService.update(theaterId, updateTheaterRequest))
                .build();
    }

//    delete theater
    @DeleteMapping("/{theaterId}")
    public ApiResponse<Void> delete(@PathVariable String theaterId) {
        theaterService.delete(theaterId);
        return ApiResponse.<Void>builder()
                .message("Delete Theater Success")
                .build();
    }
}
