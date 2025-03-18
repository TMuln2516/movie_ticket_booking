package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.CreateCouponRequest;
import com.example.booking_movie.dto.request.CreateMatchingRequest;
import com.example.booking_movie.dto.request.UpdateCouponRequest;
import com.example.booking_movie.dto.response.*;
import com.example.booking_movie.service.CouponService;
import com.example.booking_movie.service.MatchingRequestService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/matching")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MatchingRequestController {
    MatchingRequestService matchingRequestService;

    @PostMapping("/")
    public ApiResponse<Void> create(@RequestBody @Valid CreateMatchingRequest createMatchingRequest) {
        matchingRequestService.create(createMatchingRequest);
        return ApiResponse.<Void>builder()
                .message("Gửi yêu cầu ghép đôi thành công")
                .build();
    }
}
