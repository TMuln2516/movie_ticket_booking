package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.CreateCouponRequest;
import com.example.booking_movie.dto.request.GetSeatInfoRequest;
import com.example.booking_movie.dto.request.UpdateCouponRequest;
import com.example.booking_movie.dto.response.*;
import com.example.booking_movie.service.CouponService;
import com.example.booking_movie.service.SeatService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/seats")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeatController {
    SeatService seatService;

    @PostMapping("/")
    public ApiResponse<List<SeatResponse>> getInfoById(@RequestBody @Valid GetSeatInfoRequest getSeatInfoRequest) {
        return ApiResponse.<List<SeatResponse>>builder()
                .message("Lấy thông tin ghế thành công")
                .result(seatService.getSeatInfoByListId(getSeatInfoRequest))
                .build();
    }
}
