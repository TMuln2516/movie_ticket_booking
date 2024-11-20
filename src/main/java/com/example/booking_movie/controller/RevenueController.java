package com.example.booking_movie.controller;

import com.example.booking_movie.dto.response.*;
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

    @GetMapping("/ticket")
    public ApiResponse<RevenueResponse> getRevenueByDate(@RequestParam LocalDate date) {
        return ApiResponse.<RevenueResponse>builder()
                .message("Thống kê theo ngày thành công")
                .result(ticketService.getRevenueByDate(date))
                .build();
    }

    @GetMapping("/ticket/range")
    public ApiResponse<RevenueResponse> getRevenueByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ApiResponse.<RevenueResponse>builder()
                .message("Thống kê theo khoảng thời gian thành công")
                .result(ticketService.getRevenueByDateRange(startDate, endDate))
                .build();
    }

    @GetMapping("/ticket/all")
    public ApiResponse<RevenueResponse> getRevenue() {
        return ApiResponse.<RevenueResponse>builder()
                .message("Thống kê tổng doanh thu thành công")
                .result(ticketService.getRevenue())
                .build();
    }

}
