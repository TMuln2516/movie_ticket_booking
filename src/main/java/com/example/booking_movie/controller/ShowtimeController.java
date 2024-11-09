package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.*;
import com.example.booking_movie.dto.response.*;
import com.example.booking_movie.service.AuthenticationService;
import com.example.booking_movie.service.ShowtimeService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowtimeController {
    ShowtimeService showtimeService;

//    get all
    @GetMapping("/")
    public ApiResponse<List<GetAllShowtimeResponse>> getAll(@RequestBody @Valid GetAllShowTimeRequest getAllShowTimeRequest) {
        return ApiResponse.<List<GetAllShowtimeResponse>>builder()
                .message("Lấy tất cả suất chiếu thành công")
                .result(showtimeService.getAll(getAllShowTimeRequest))
                .build();
    }

//    create
    @PostMapping("/")
    public ApiResponse<CreateShowtimeResponse> create(@RequestBody @Valid CreateShowtimeRequest createShowtimeRequest) {
        return ApiResponse.<CreateShowtimeResponse>builder()
                .message("Create Showtime Success")
                .result(showtimeService.create(createShowtimeRequest))
                .build();
    }

//    update
    @PutMapping("/{showtimeId}")
    public ApiResponse<UpdateShowtimeResponse> update(@PathVariable String showtimeId, @RequestBody @Valid UpdateShowtimeRequest updateShowtimeRequest) {
        return ApiResponse.<UpdateShowtimeResponse>builder()
                .message("Cập nhật suất chiếu thành công")
                .result(showtimeService.update(showtimeId, updateShowtimeRequest))
                .build();
    }
}
