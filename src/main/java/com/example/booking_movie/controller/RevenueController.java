package com.example.booking_movie.controller;

import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.dto.response.RevenueResponse;
import com.example.booking_movie.service.TicketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/revenues")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RevenueController {
    TicketService ticketService;

    @PostMapping("/byDate")
    public ApiResponse<List<RevenueResponse>> getRevenueByDate() {
        return ApiResponse.<List<RevenueResponse>>builder()
                .message("Lấy Top 3 phim có doanh thu cao nhất theo ngày thành công")
                .result(ticketService.getTop3MoviesRevenueIn1Day())
                .build();
    }

    @PostMapping("/byWeek")
    public ApiResponse<List<RevenueResponse>> getRevenueByWeek() {
        return ApiResponse.<List<RevenueResponse>>builder()
                .message("Lấy Top 3 phim có doanh thu cao nhất theo 7 ngày thành công")
                .result(ticketService.getTop3MoviesRevenueIn7Days())
                .build();
    }

    @PostMapping("/byMonth")
    public ApiResponse<List<RevenueResponse>> getRevenueByMonth() {
        return ApiResponse.<List<RevenueResponse>>builder()
                .message("Lấy Top 3 phim có doanh thu cao nhất theo tháng thành công")
                .result(ticketService.getTop3MoviesRevenueInMonth())
                .build();
    }
}
